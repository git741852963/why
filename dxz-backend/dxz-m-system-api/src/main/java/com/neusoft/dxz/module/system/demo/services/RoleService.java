package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.dto.CommonDto;

import java.util.List;

/**
 * 角色维护接口。
 * <p/>
 * 摘要：提供对角色的维护。
 *
 * @author andy.jiao@msn.com
 */
public interface RoleService {

    /**
     * 查询角色数据，初始化角色管理页面
     * <p/>
     * 查询角色数据，初始化角色管理页面，返回操作应答
     *
     * 对应组件：角色管理[console/authority/roles_config]
     *
     * @return 操作应答
     */
    Response<CommonDto<Role>> load();

    /**
     * 查询所有角色数据
     *
     * @return 角色列表
     */
    Response<List<Role>> allRoles();

    /**
     * 根据角色ID列表查找角色
     *
     * @return 角色列表
     */
    Response<List<Role>> findByIds(List<Long> roleIds);

    /**
     * 根据角色ID查找角色
     *
     * @return 角色信息
     */
    Response<Role> findById(Long id);

    /**
     * 添加角色
     * <p/>
     * 添加角色(角色数据发生变化时先清除Redis角色缓存数据)，返回操作应答
     *
     * @param base 角色数据
     * @return 操作应答
     */
    Response<Role> add(Role base);

    /**
     * 编辑角色
     * <p/>
     * 编辑角色(角色数据发生变化时先清除Redis角色缓存数据)，返回操作应答
     *
     * @param base 角色数据
     * @return 操作应答
     */
    Response<Role> edit(Role base);

    /**
     * 设定默认角色
     * <p/>
     * 根据角色类型设置默认角色(角色数据发生变化时先清除Redis角色缓存数据)，每种角色类型只能存在一个默认角色，
     * 默认角色系统会自动分配给对应类型的用户，返回操作应答
     *
     * @param base 角色数据
     * @return 操作应答
     */
    Response<Role> editDefault(Role base);

    /**
     * 删除角色
     * <p/>
     * 删除角色(角色数据发生变化时先清除Redis角色缓存数据)，返回操作应答
     * <p/>
     * 以下参数必须：<br>
     * Role.id<br>
     *
     * @param id 角色ID
     * @return 操作应答
     */
    Response<Boolean> remove(Long id);

    /**
     * 根据角色类型查找默认角色
     * <p/>
     * 如果没有默认角色，返回错误消息。
     *
     * @return 默认角色
     */
    Response<Role> findDefaultRole(RoleType type);

    /**
     * 查找用户注册时绑定的默认角色(从Redis中查询)<br>
     * <br>
     * <p/>
     * 查找用户注册时绑定的默认角色(从Redis中查询)，返回操作应答<br>
     * <br>
     * <p/>
     * 以下参数非必须：<br>
     * 无<br>
     *
     * @return 操作应答
     */
    Response<Role> findUserDefaultRoleCache();

//    /**
//     * 查找商家入驻时绑定的默认角色(从Redis中查询)
//     * <p/>
//     * 查找商家入驻时绑定的默认角色(从Redis中查询)，返回操作应答
//     *
//     * @return 操作应答
//     */
//    Response<Role> findStoreDefaultRoleCache();

    //    /**
    //     * 查找平台商家绑定的默认角色(从Redis中查询)
    //     * <p/>
    //     * 查找平台商家绑定的默认角色(从Redis中查询)，返回操作应答
    //     *
    //     * @return 操作应答
    //     */
    //    Response<Role> findPlatformSellerDefaultRoleCache();

    /**
     * 加载角色详细信息(从Redis中查询)
     * <p/>
     * 根据ID获取角色详细信息(从Redis中查询)，返回操作应答
     * <p/>
     * 以下参数必须：<br>
     * roleId<br>
     *
     * @param roleId 角色id
     * @return 操作应答
     */
    Response<Role> detailCache(Long roleId);

    /**
     * 查询角色名字
     * <p/>
     * 根绝角色ID列表查询角色名字（商家子账号管理），返回操作应答
     *
     * @param roleIds 角色ID列表
     * @return 操作应答
     */
    Response<List<String>> selectRoleNames(List<String> roleIds);

    /**
     * 查询商家相关角色
     * <p/>
     * 查询商家相关角色（商家子账号管理），返回操作应答
     *
     * @return 操作应答
     */
    Response<List<Role>> selectStoreRoles();
}
