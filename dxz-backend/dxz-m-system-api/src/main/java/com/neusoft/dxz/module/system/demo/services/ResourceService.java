package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.model.Resource;
import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;

import java.util.List;

/**
 * 资源维护接口。
 * <p/>
 * 摘要：提供对资源数据的维护，包含资源数据的缓存操作。
 *
 * @author andy.jiao@msn.com
 */
public interface ResourceService {

    /**
     * 资源管理页面初始化接口方法
     * <p/>
     * 资源管理页面初始化接口，根据资源类型获取资源列表数据（不需要从Redis中读取，直接从DB读取数据）
     *
     * 对应组件：资源管理[console/authority/resources_config]
     *
     * @param category 资源类型
     * @return 资源列表
     */
    Response<CommonDto<Resource>> load(Integer category);

    /**
     * 添加资源节点方法
     * <p/>
     * 根据资源数据创建资源，返回应答内容
     *
     * @param base 资源数据
     * @return 操作应答
     */
    Response<Resource> create(Resource base);

    /**
     * 编辑资源节点方法
     * <p/>
     * 根据资源数据修改资源，返回应答内容
     *
     * @param base 资源数据
     * @return 操作应答
     */
    Response<Resource> update(Resource base);

    /**
     * 删除资源节点方法
     * <p/>
     * 根据资源ID删除资源，返回应答内容
     * <p/>
     * 以下参数必须：<br>
     * AuthorityModel.id<br>
     *
     * @param base 资源数据
     * @return 操作应答
     */
    Response<Resource> remove(Resource base);

    /**
     * 更新资源节点排序方法
     * <p/>
     * 根据资源排序数据更新资源顺序，返回应答内容
     * <p/>
     * 以下参数必须：<br>
     * CommonDto<AuthorityModel>.modelData<br>
     *
     * @param base 资源排序数据
     * @return 操作应答
     */
    Response<CommonDto<Resource>> editDisplay(CommonDto<Resource> base);

    /**
     * 查询资源数据
     * <p/>
     * 根据资源类型及资源层级查询资源，首先经过redis缓存中查询，查询结果同步更新redis，返回应答内容
     * <p/>
     * 以下参数必须：<br>
     * category<br>
     * level<br>
     *
     * @param category 资源类型
     * @param level    资源级别
     * @return modelData操作应答
     */
    Response<CommonDto<Resource>> detailCache(Integer category, Integer level);

    /**
     * 取得成员的所有资源(从Redis中取得)
     * <p/>
     * 根据当前登录用户取得其的所有资源(从Redis中取得)，返回应答内容
     * <p/>
     * 对应组件：菜单栏[console/common/sidebar]
     *
     * @param baseUser 当前登录用户基本信息
     * @return 用户资源列表
     */
    Response<List<Resource>> findByUser(BaseUser baseUser);

    /**
     * 取得一级资源分类
     * <p/>
     * 根据资源类型获取所有一级资源列表，返回应答内容
     * <p/>
     * 以下参数非必须：<br>
     * 无<br>
     *
     * @param category 资源分类
     * @return 所有一级资源列表
     */
    Response<List<Resource>> findTopLevelResource(Integer category);

    /**
     * 取得资源分类下的所有子资源
     * <p/>
     * 根据顶级资源分类及父资源ID获取所有子资源列表，返回应答内容
     * <p/>
     * 以下参数非必须：<br>
     * 无<br>
     *
     * @param category 资源分类
     * @param parentId 父资源id
     * @return 所有子资源列表
     */
    Response<List<Resource>> findChildResourceById(Integer category,
                                                   Long parentId);


    /**
     * 获取资源对应的角色列表
     * <p/>
     *
     * @return 所有资源角色列表
     */
    Response<List<NameValuePair>> findAllResRoles();
}
