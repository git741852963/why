package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.ResourceDao;
import com.neusoft.dxz.module.system.demo.dao.ResourceRedisDao;
import com.neusoft.dxz.module.system.demo.dao.RoleResourceDao;
import com.neusoft.dxz.module.system.demo.enums.ResourceType;
import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.system.demo.manager.ResourceManager;
import com.neusoft.dxz.module.system.demo.model.Resource;
import com.neusoft.dxz.module.system.demo.model.RoleResource;
import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;
import com.neusoft.dxz.module.system.demo.validator.group.ResourceGroup;
import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.neusoft.features.common.utils.Arguments.notNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * 资源维护接口实现。
 *
 * @author andy.jiao@msn.com
 */
@Service
public class ResourceServiceImpl extends BaseService implements ResourceService {

    /** 资源管理Dao */
    @Autowired
    private ResourceDao resourceDao;

    /** 角色-资源管理Dao */
    @Autowired
    private RoleResourceDao roleResourceDao;

    /** 资源管理Redis Dao */
    @Autowired
    private ResourceRedisDao resourceRedisDao;

    /** 资源管理 */
    @Autowired
    private ResourceManager resourceManager;

    /**
     * 资源管理页面初始化（不需要从Redis中读取，直接从DB读取数据）
     *
     * @param category 资源区分
     * @return 资源数据
     */
    @Override
    public Response<CommonDto<Resource>> load(Integer category) {

        Response<CommonDto<Resource>> response = new Response<>();

        try {
//            checkArgument(ResourceType.from(category) != null, "system.resource.param.category.invalid");
            if (category == null) {
                category = ResourceType.CONSOLE.value();
            }

            CommonDto<Resource> param = new CommonDto<>();
            param.addFlexibleData("category", category);
            param.addFlexibleData("level", 0);
            param.addFlexibleData("types", ResourceType.toList());

            // 查询父节点
            List<Resource> resourceHierarchy = resourceDao.selectList(param);

            // 查询子节点
            param.addFlexibleData("level", 1);
            List<Resource> resourceChildren = resourceDao.selectList(param);

            for (Resource parent : resourceHierarchy) {
                for (Resource child : resourceChildren) {
                    if (parent.getId().equals(child.getParentId())) {
                        parent.addChild(child);
                    }
                }
            }

            param.setModelData(resourceHierarchy);
            response.setResult(param);
        } catch (IllegalArgumentException e) {
            log.error("failed to load resources, category={}, cause:{}", category, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to load resources, category={}, cause:{}", category, Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }

    /**
     * 添加节点
     *
     * @param resource 权限节点
     * @return 处理结果
     */
    @Override
    public Response<Resource> create(Resource resource) {
        Response<Resource> response = new Response<>();

        try {
            validate(resource, ResourceGroup.CREATE.class);

            // 判断资源是否重复
            CommonDto<Resource> param = new CommonDto<>();
            param.addFlexibleData("category", resource.getCategory());
            param.addFlexibleData("parentId", resource.getParentId());
            param.addFlexibleData("name", resource.getName());
            Resource res = resourceDao.selectOne(param);
            if (notNull(res)) {
                response.setError("system.resource.node.duplicated");
                return response;
            }

            // 查询最大display
            param.addFlexibleData("parentId", resource.getParentId());
            param.addFlexibleData("level", resource.getLevel());
            Long maxDisplay = resourceDao.max(param);

            // 计算顺序
            int display = (int) ((maxDisplay == null ? 0L : maxDisplay) + 1);
            resource.setDisplay(display);

            // 插入权限数据
            resourceManager.add(resource);
            response.setResult(resource);
        } catch (IllegalArgumentException e) {
            log.error("failed to create resource, cause:{}", e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to create resource, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.resource.create.fail");
        }

        return response;
    }

    /**
     * 编辑节点
     *
     * @param resource 权限节点
     * @return 处理结果
     */
    @Override
    public Response<Resource> update(Resource resource) {

        Response<Resource> response = new Response<>();

        try {
            validate(resource, ResourceGroup.UPDATE.class);

            // 判断资源是否重复
            CommonDto<Resource> param = new CommonDto<>();
            param.addFlexibleData("category", resource.getCategory());
            param.addFlexibleData("name", resource.getName());
            param.addFlexibleData("parentId", resource.getParentId());
            Resource res = resourceDao.selectOne(param);
            if (res != null && !res.getId().equals(resource.getId())) {
                response.setError("system.resource.resource.duplicated");
                return response;
            }

            // 更新权限数据
            resourceManager.update(resource);
            response.setResult(resource);
        } catch (IllegalArgumentException e) {
            log.error("failed to update resource, cause:{}", e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to update resource, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.resource.update.fail");
        }

        return response;
    }

    /**
     * 删除节点
     *
     * @param resource 权限节点
     * @return 处理结果
     */
    @Override
    public Response<Resource> remove(Resource resource) {

        Response<Resource> response = new Response<>();

        try {
            validate(resource, ResourceGroup.DELETE.class);

            // 获取resource及下级resource列表
            List<Resource> resourceList = Lists.newArrayList();
            if (resource.getLevel() == 0) {
                CommonDto<Resource> param = new CommonDto<>();
                param.addFlexibleData("parentId", resource.getId());
                resourceList = resourceDao.selectList(param);
            } else {
                resourceList.add(resource);
            }

            // 加载所有角色权限关系
            CommonDto<RoleResource> dto = new CommonDto<>();
            List<RoleResource> roleResourceList = roleResourceDao.findByCondition(dto);

            // 查找是否存在被使用的权限
            for (Resource resourceModel : resourceList) {
                for (RoleResource roleResource : roleResourceList) {
                    if (roleResource.getResourceId().equals(resourceModel.getId())) {
                        response.setError("system.resource.res.in.use");
                        return response;
                    }
                }
            }

            resourceManager.delete(resource);
            response.setResult(resource);
        } catch (IllegalArgumentException e) {
            log.error("failed to delete resource, cause:{}", e.getMessage());
            response.setError(e.getMessage());
        }  catch (Exception e) {
            log.error("failed to delete resource, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.resource.delete.fail");
        }
        return response;
    }

    /**
     * 更新节点顺序
     *
     * @param resources 权限节点
     * @return 处理结果
     */
    @Override
    public Response<CommonDto<Resource>> editDisplay(CommonDto<Resource> resources) {

        Response<CommonDto<Resource>> response = new Response<>();

        try {
            checkArgument(resources != null && resources.getModelData() != null, "illegal.param.null");
            checkArgument(resources.getModelData().size() == 2, "illegal.param.null");
            for (Resource resource: resources.getModelData()) {
                validate(resource, BaseValidatorGroup.UPDATE.class);
            }

            // 更新节点顺序
            resourceManager.sort(resources.getModelData().get(0), resources.getModelData().get(1));
            response.setResult(resources);
        } catch (IllegalArgumentException e) {
            log.error("failed to sort resources, cause:{}", e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to sort resources, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.resource.sort.fail");
        }
        return response;
    }

    /**
     * 查询权限数据
     *
     * @param category 权限区分
     * @param level    级别
     * @return 节点数据
     */
    @Override
    public Response<CommonDto<Resource>> detailCache(Integer category, Integer level) {

        Response<CommonDto<Resource>> response = new Response<>();

        try {
            checkArgument(ResourceType.from(category) != null, "system.resource.param.category.invalid");
            checkArgument(level != null, "illegal.param.null");

            // 从redis中获取资源列表
            String redisKey = String.valueOf(category) + ":" + String.valueOf(level);
            List<Resource> resources = this.resourceRedisDao.findResourcesByCategory(redisKey);

            if (resources == null) {
                // 如果redis中没有资源数据，尝试从DB中加载资源
                CommonDto<Resource> param = new CommonDto<>();
                param.addFlexibleData("category", category);
                param.addFlexibleData("level", level);
                resources = resourceDao.selectList(param);

                if (resources == null || resources.size() == 0) {
                    // DB及缓存中都没有相关数据
                    response.setError("system.resource.resource.empty");
                    return response;
                } else {
                    // 资源信息保存到缓存
                    this.resourceRedisDao.setResourcesByCategory(redisKey, resources);
                }
            }
            CommonDto<Resource> result = new CommonDto<>();
            result.setModelData(resources);
            response.setResult(result);
        } catch (IllegalArgumentException e) {
            log.error("failed to load resources, level={}, cause:{}", category, level, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to load resources, category={}, level={}, cause:{}", category, level, Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }

    /**
     * 取得成员的所有权限(从Redis中取得)
     *
     * @param baseUser 成员信息
     * @return 成员的所有权限
     */
    @Override
    public Response<List<Resource>> findByUser(BaseUser baseUser) {

        Response<List<Resource>> response = new Response<>();

        try {
            List<Resource> result;
            String roles = "";
            List<Long> roleIds = Lists.newArrayList();

            if (!baseUser.isSuperMan()) {
                checkState(baseUser.getRoles() != null, "system.resource.user.resource.empty");
                for (Long roleId : baseUser.getRoles()) {
                    roleIds.add(roleId);
                }
                roles = Joiner.on('@').skipNulls().join(roleIds);
            }

            String redisKey = Strings.isNullOrEmpty(roles) ? "user" : roles + ":"
                    + String.valueOf(baseUser.isManager()) + ":"
                    + String.valueOf(baseUser.getType());

            result = this.resourceRedisDao.findResourcesByRoleId(redisKey);

            if (result == null) {
                List<Resource> parents;
                List<Resource> children;

                // 只有超级管理员可以看到所有菜单，超级管理员可以看做管理员的一个特例
                if (baseUser.isSuperMan()) {
                    parents = this.resourceDao.findTopLevelResources(ResourceType.CONSOLE.value());
                    children = this.resourceDao.all();
                } else {
                    RoleType roleType = RoleType.from(baseUser.getType());
                    ResourceType resType = ResourceType.CONSOLE;
                    switch (roleType) {
                        case USER:
                            return response;
                        case MANAGER:
                            resType = ResourceType.CONSOLE;
                            break;
//                        case COMPANY:
//                        case CONTRACTOR:
//                        case ADSP:
//                            resType = ResourceType.COMPANY;
//                            break;
                    }
                    parents = this.resourceDao.findTopLevelResources(resType.value());
                    children = this.resourceDao.findResourcesByRoleId(roleIds);
                }

                // 将父菜单与子菜单做映射
                for (Resource parent : parents) {
                    for (Resource child : children) {
                        if (parent.getId().equals(child.getParentId())) {
                            parent.addChild(child);
                        }
                    }
                }

                // 移除所有没有子菜单的顶级菜单
                for (int i = parents.size() - 1; i >= 0; i--) {
                    if (parents.get(i).getChildren() == null || parents.get(i).getChildren().size() == 0) {
                        parents.remove(i);
                    }
                }

                if (parents.size() == 0) {
                    response.setError("system.resource.user.resource.empty");
                } else {
                    this.resourceRedisDao.setResourcesByRoleId(redisKey, parents);
                    response.setResult(parents);
                }
            } else {
                response.setResult(result);
            }
        } catch (Exception e) {
            log.error("failed to load user resources, userId={}, roles={}, cause:{}", baseUser.getId(), baseUser.getRoles(), Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }

    /**
     * 取得一级权限分类
     *
     * @param category 权限类型
     * @return 一级权限分类数据
     */
    public Response<List<Resource>> findTopLevelResource(Integer category) {

        Response<List<Resource>> response = new Response<>();

        try {
            checkArgument(ResourceType.from(category) != null, "system.resource.param.category.invalid");

            CommonDto<Resource> param = new CommonDto<>();
            param.addFlexibleData("category", category);
            param.addFlexibleData("level", 0);
            // 查询一级权限分类数据
            List<Resource> resources = resourceDao.selectList(param);

            if (resources.isEmpty()) {
                response.setError("system.resource.resource.empty");
            } else {
                response.setResult(resources);
            }
        } catch (IllegalArgumentException e) {
            log.error("failed to load top level resources, category={}, cause:{}", category, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to load top level resources, category={}, cause:{}", category, Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }

    /**
     * 取得某分类下的权限数据(在菜单中显示的权限数据)
     *
     * @param category 权限类型
     * @param parentId 分类ID
     * @return 权限数据
     */
    public Response<List<Resource>> findChildResourceById(Integer category, Long parentId) {

        Response<List<Resource>> response = new Response<>();

        try {
            checkArgument(ResourceType.from(category) != null, "system.resource.param.category.invalid");
            checkArgument(parentId != null, "system.resource.param.parent.null");

            CommonDto<Resource> param = new CommonDto<>();
            param.addFlexibleData("category", category);
            param.addFlexibleData("parentId", parentId);
            param.addFlexibleData("level", 1);
            param.addFlexibleData("sidebarShow", true);

            // 查询权限数据
            List<Resource> resources = resourceDao.selectList(param);
            if (resources.isEmpty()) {
                response.setError("取得权限数据:数据不存在");
            } else {
                response.setResult(resources);
            }
        } catch (IllegalArgumentException e) {
            log.error("failed to load resources, category={}, parentId={}, cause:{}", category, parentId, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to load resources, category={}, parentId={}, cause:{}", category, parentId, Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }

    @Override
    public Response<List<NameValuePair>> findAllResRoles() {
        Response<List<NameValuePair>> response = new Response<>();

        try {
            // 查询权限数据
            List<NameValuePair> resources = roleResourceDao.findAllResRoles();
            response.setResult(resources);
        } catch (Exception e) {
            log.error("failed to load resources & roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.resource.query.fail");
        }

        return response;
    }
}
