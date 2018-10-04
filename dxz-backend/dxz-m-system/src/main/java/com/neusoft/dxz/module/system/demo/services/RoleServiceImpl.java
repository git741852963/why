package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.RoleDao;
import com.neusoft.dxz.module.system.demo.dao.RoleRedisDao;
import com.neusoft.dxz.module.system.demo.dao.RoleResourceDao;
import com.neusoft.dxz.module.system.demo.enums.ResourceType;
import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.system.demo.manager.RoleManager;
import com.neusoft.dxz.module.system.demo.model.Resource;
import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.dxz.module.system.demo.model.RoleResource;
import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.neusoft.features.dto.CommonDto;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Role Service。
 *
 * @author andy.jiao@msn.com
 */
@Service
public class RoleServiceImpl extends BaseService implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleRedisDao roleRedisDao;

    @Autowired
    private RoleResourceDao roleResourceDao;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleManager roleManager;

    /**
     * 查询角色数据，初始化角色管理页面
     *
     * @return 角色数据
     */
    @Override
    public Response<CommonDto<Role>> load() {
        Response<CommonDto<Role>> response = new Response<>();

        try {
            CommonDto<Role> roleDto = new CommonDto<>();

            // 查询所有角色类型
            roleDto.addFlexibleData("role_types", RoleType.toList());

            // 查询所有角色
            List<Role> roleList = roleDao.selectList(roleDto);
            roleDto.setModelData(roleList);

            // 查询角色权限关系
            CommonDto<RoleResource> dto = new CommonDto<>();
            List<RoleResource> roleAuthorityList = roleResourceDao.findByCondition(dto);

            for (Role role : roleList) {
                for (RoleResource roleResource : roleAuthorityList) {
                    if (role.getId().equals(roleResource.getRoleId())) {
                        role.addRoleResource(roleResource);
                    }
                }
            }

            List<Map<String, Object>> allTypeResources = Lists.newArrayList();

            for (ResourceType resType: ResourceType.values()) {
                // 加载所有类型的资源列表（目前只有管理后台一种类型）
                Response<CommonDto<Resource>> result = resourceService.load(resType.value());
                checkState(result.isSuccess(), result.getError());

                Map<String, Object> resMap = Maps.newHashMap();
                resMap.put("resource_type_name", resType.name());
                resMap.put("resource_type_value", resType.value());
                resMap.put("resource_type_desc", resType.toString());
                resMap.put("resources", result.getResult().getModelData());

                List<Integer> roleTypes = Lists.newArrayList();
                for (RoleType type : RoleType.values()) {
                    if (type.resource() == resType) {
                        roleTypes.add(type.value());
                    }
                }
                resMap.put("role_types", roleTypes);
                allTypeResources.add(resMap);
            }

            roleDto.addFlexibleData("resources_config", allTypeResources);

            response.setResult(roleDto);
        } catch (IllegalArgumentException e) {
            log.error("failed to load roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to load roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.fail");
        }

        return response;
    }

    @Override
    public Response<List<Role>> allRoles() {
        Response<List<Role>> response = new Response<>();

        try {
            CommonDto<Role> roleDto = new CommonDto<>();

            // 查询所有角色
            List<Role> roleList = roleDao.selectList(roleDto);
            response.setResult(roleList);
        } catch (Exception e) {
            log.error("failed to find all roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.fail");
        }

        return response;
    }

    @Override
    public Response<List<Role>> findByIds(List<Long> roleIds) {
        Response<List<Role>> response = new Response<>();

        try {
            checkArgument(roleIds != null && roleIds.size() > 0, "system.role.param.id.null");

            // 根据ID列表查询角色
            List<Role> roleList = roleDao.findByIds(roleIds);
            response.setResult(roleList);
        } catch (Exception e) {
            log.error("failed to find roles by ids, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.fail");
        }

        return response;
    }

    @Override
    public Response<Role> findById(Long id) {
        Response<Role> response = new Response<>();

        try {
            checkArgument(id != null, "system.role.param.id.null");

            // 根据ID查询角色
            Role role = roleDao.findById(id);
            response.setResult(role);
        } catch (Exception e) {
            log.error("failed to find roles by ids, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.fail");
        }

        return response;
    }

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return 处理结果
     */
    @Override
    public Response<Role> add(Role role) {
        Response<Role> response = new Response<>();

        try {
            validate(role, BaseValidatorGroup.CREATE.class);

            CommonDto<Role> roleDto = new CommonDto<>();
            roleDto.addFlexibleData("name", role.getName());
            List<Role> existRoles = roleDao.selectList(roleDto);
            checkState(existRoles.size() == 0, "system.role.name.already.exist");

            roleManager.create(role);
            response.setResult(role);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to create role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to create role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError("system.role.create.fail");
        }

        return response;
    }

    /**
     * 编辑角色
     *
     * @param role 角色信息
     * @return 处理结果
     */
    @Override
    public Response<Role> edit(Role role) {
        Response<Role> response = new Response<>();

        try {
            validate(role, BaseValidatorGroup.UPDATE.class);

            // 根据ID查询角色
            Role roleInDB = roleDao.findById(role.getId());
            checkState(roleInDB != null, "system.role.record.not.exist");
            // 角色类型不能修改
            checkArgument(role.getCategory() == roleInDB.getCategory(), "system.role.category.can.not.modify");

            // 判断是否有重名权限
            CommonDto<Role> roleDto = new CommonDto<>();
            roleDto.addFlexibleData("excludeId", role.getId());
            roleDto.addFlexibleData("name", role.getName());
            List<Role> existRoles = roleDao.selectList(roleDto);
            checkState(existRoles.size() == 0, "system.role.name.already.exist");

            roleManager.update(role);
            response.setResult(role);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to update role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to update role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError("system.role.update.fail");
        }

        return response;
    }

    /**
     * 设定默认角色
     *
     * @param role 角色信息
     * @return 处理结果
     */
    @Override
    public Response<Role> editDefault(Role role) {
        Response<Role> response = new Response<>();

        try {
            validate(role, BaseValidatorGroup.UPDATE.class);

            roleManager.editDefault(role);
            response.setResult(role);
        } catch (IllegalArgumentException e) {
            log.error("failed to set default role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to set default role, role={}, cause:{}", role, Throwables.getStackTraceAsString(e));
            response.setError("system.role.set.default.fail");
        }

        return response;
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 处理结果
     */
    @Override
    public Response<Boolean> remove(Long id) {
        Response<Boolean> response = new Response<>();

        try {
            checkArgument(id != null, "system.role.param.id.null");

            roleManager.delete(id);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to delete role, id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to delete role, id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("system.role.delete.fail");
        }

        return response;
    }

    @Override
    public Response<Role> findDefaultRole(RoleType type) {
        Response<Role> response = new Response<>();

        try {
            Role result = this.roleRedisDao.findDefaultRole(String.valueOf(type.value()));
            if (result == null) {
                CommonDto<Role> roleDto = new CommonDto<>();
                roleDto.addFlexibleData("category", type.value());
                roleDto.addFlexibleData("isDefault", true);
                List<Role> roles = this.roleDao.selectList(roleDto);
                if (roles == null || roles.size() != 1) {
                    response.setError("system.role.default.role.invalid");  //找不到默认的角色
                    return response;
                } else {
                    this.roleRedisDao.setDefaultRole(String.valueOf(type.value()), roles.get(0));
                    result = roles.get(0);
                }
            }
            response.setResult(result);
        } catch (IllegalArgumentException e) {
            log.error("failed to find default role, role type={}, cause:{}", type, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find default role, role type={}, cause:{}", type, Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.default.by.role.type.fail");
        }

        return response;
    }

    /**
     * 查找用户注册时绑定的默认角色(从Redis中查询)
     *
     * @return 用户注册时绑定的默认角色
     */
    @Override
    public Response<Role> findUserDefaultRoleCache() {
        Response<Role> response = new Response<>();

        try {
            Role result = this.roleRedisDao.findDefaultRole(String.valueOf(RoleType.USER.value()));

            if (result == null) {
                //TODO:这里应该按照RoleType检索还是ResourceType
                CommonDto<Role> param = new CommonDto<>();
                param.addFlexibleData("category", RoleType.USER.value());
                param.addFlexibleData("isDefault", true);
                List<Role> roles = this.roleDao.selectList(param);

                if (roles == null || roles.size() != 1) {
                    response.setError("system.role.default.role.invalid");  //找不到默认的角色
                    return response;
                } else {
                    this.roleRedisDao.setDefaultRole(String.valueOf(RoleType.USER.value()), roles.get(0));
                    result = roles.get(0);
                }
            }

            response.setResult(result);
        } catch (IllegalArgumentException e) {
            log.error("failed to find user default role, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find user default role, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.user.default.fail");
        }

        return response;
    }

//    /**
//     * 查找商家入驻时绑定的默认角色(从Redis中查询)
//     *
//     * @return 商家入驻时绑定的默认角色
//     */
//    @Override
//    public Response<Role> findStoreDefaultRoleCache() {
//        Response<Role> response = new Response<>();
//
//        try {
//            Role result = this.roleRedisDao.findDefaultRole(String.valueOf(RoleType.COMPANY.value()));
//            if (result == null) {
//                CommonDto<Role> roleDto = new CommonDto<>();
//                roleDto.addFlexibleData("category", RoleType.COMPANY.value());
//                roleDto.addFlexibleData("isDefault", true);
//                List<Role> roles = this.roleDao.selectList(roleDto);
//                if (roles == null || roles.size() != 1) {
//                    response.setError("system.role.default.role.invalid");  //找不到默认的角色
//                    return response;
//                } else {
//                    this.roleRedisDao.setDefaultRole(String.valueOf(RoleType.COMPANY.value()), roles.get(0));
//                    result = roles.get(0);
//                }
//            }
//            response.setResult(result);
//        } catch (IllegalArgumentException e) {
//            log.error("failed to find company default role, cause:{}", Throwables.getStackTraceAsString(e));
//            response.setError(e.getMessage());
//        } catch (Exception e) {
//            log.error("failed to find company default role, cause:{}", Throwables.getStackTraceAsString(e));
//            response.setError("system.role.query.company.default.fail");
//        }
//
//        return response;
//    }

    //    /**
    //     * 查找平台商家绑定的默认角色(从Redis中查询)
    //     *
    //     * @return 平台商家绑定的默认角色
    //     */
    //    @Override
    //    public Response<Role> findPlatformSellerDefaultRoleCache() {
    //        Response<CommonDto<Role>> response = new Response<>();
    //
    //        try {
    //
    //            Role result = this.roleRedisDao.findDefaultRole(BasicEnums.ROLE_TYPE_PLATFORM_SELLER.toString());
    //            if (result == null) {
    //                CommonDto<Role> roleDto = new CommonDto<>();
    //                roleDto.addFlexibleData("category", BasicEnums.ROLE_TYPE_PLATFORM_SELLER.value());
    //                roleDto.addFlexibleData("isDefault", true);
    //                List<Role> roles = this.roleDao.isExist(roleDto);
    //                if (roles == null || roles.size() != 1) {
    //                    return this.produce(ResponseDescription.RESPONSE_FAILED_2, new Object[]{"平台商家", "找不到默认的角色"});
    //                } else {
    //                    this.roleRedisDao.setDefaultRole(BasicEnums.ROLE_TYPE_PLATFORM_SELLER.toString(), roles.get(0));
    //                    result = roles.get(0);
    //                }
    //            }
    //            return this.produce(ResponseDescription.RESPONSE_SUCCESS_1, result, new Object[]{"查询平台商家使用的默认角色"});
    //
    //        } catch (IllegalArgumentException e) {
    //            log.error("failed to load resources, category={}, cause:{}", category, Throwables.getStackTraceAsString(e));
    //            response.setError(e.getMessage());
    //        } catch (Exception e) {
    //            log.error("failed to load resources, category={}, cause:{}", category, Throwables.getStackTraceAsString(e));
    //            response.setError("system.resource.query.fail");
    //        }
    //
    //        return response;
    //    }

    /**
     * 加载角色详细信息(从Redis中查询)
     *
     * @param roleId 角色ID
     * @return 角色详细信息
     */
    @Override
    public Response<Role> detailCache(Long roleId) {

        Response<Role> response = new Response<>();

        try {
            checkArgument(roleId != null, "system.role.param.id.null");

            Role result = this.roleRedisDao.findRoleDetailInfo(String.valueOf(roleId));
            if (result == null) {
                CommonDto<Role> base = new CommonDto<>();
                base.addFlexibleData("roleId", roleId);
                Role role = roleDao.selectOne(base);
                if (role == null) {
                    response.setError("system.role.record.not.exist"); //数据不存在
                    return response;
                }

                CommonDto<RoleResource> param = new CommonDto<>();
                param.setFlexibleData(base.getFlexibleData());
                // 查询角色权限关系
                List<RoleResource> roleAuthorityList = roleResourceDao.findByCondition(param);
                role.setRoleResources(roleAuthorityList);

                // 查询权限数据
                RoleType roleType = RoleType.from(role.getCategory());
                // 角色类型非法
                checkState(roleType != null, "system.role.category.invalid");
                Integer category = 0;
                switch (roleType) {
                    case MANAGER:
                        category = ResourceType.CONSOLE.value();
                        break;
//                    case COMPANY:
//                    case CONTRACTOR:
//                    case ADSP:
//                        category = ResourceType.COMPANY.value();
//                        break;
//                    case USER:
//                        category = ResourceType.USER.value();
//                        break;
                }

                Response<CommonDto<Resource>> cachedResources = resourceService.detailCache(category, 1);
                CommonDto<Resource> authorityDto = cachedResources.getResult();
                List<Resource> authorityList = authorityDto.getModelData();

                for (RoleResource roleResource : roleAuthorityList) {
                    for (Resource resource : authorityList) {
                        if (roleResource.getResourceId().equals(resource.getId())) {
                            String uri = resource.getUri();
                            if (!Strings.isNullOrEmpty(uri) && uri.contains("?")) {
                                int index = uri.indexOf("?");
                                resource.setUri(uri.substring(0, index));
                            }
                            role.addResource(resource);
                        }
                    }
                }
                this.roleRedisDao.setRoleDetailInfo(String.valueOf(roleId), role);
                result = role;
            }

            response.setResult(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to find roles by id, id={}, cause:{}", roleId, Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find roles by id, id={}, cause:{}", roleId, Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.fail");
        }

        return response;
    }

    /**
     * 查询角色名字（for 商家子账号管理）
     *
     * @param roleIds role ids
     * @return 角色名列表
     */
    @Override
    public Response<List<String>> selectRoleNames(List<String> roleIds) {
        Response<List<String>> response = new Response<>();

        try {
            checkArgument(roleIds == null || roleIds.size() == 0, "system.role.param.id.null");
            List<String> list = roleDao.selectRoleNames(roleIds);
            response.setResult(list);
        } catch (IllegalArgumentException e) {
            log.error("failed to find role names by ids, ids={}, cause:{}", roleIds, Throwables.getStackTraceAsString(
                    e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find role names by ids, ids={}, cause:{}", roleIds, Throwables.getStackTraceAsString(
                    e));
            response.setError("system.role.query.names.fail");
        }

        return response;
    }

    /**
     * 查询商家相关角色（for 商家子账号管理）
     *
     * @return 角色列表
     */
    @Override
    public Response<List<Role>> selectStoreRoles() {
        Response<List<Role>> response = new Response<>();

        try {
            List<Role> list = roleDao.selectStoreRoles();
            response.setResult(list);
        } catch (IllegalArgumentException e) {
            log.error("failed to find company roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find company roles, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.role.query.company.roles.fail");
        }

        return response;
    }
}
