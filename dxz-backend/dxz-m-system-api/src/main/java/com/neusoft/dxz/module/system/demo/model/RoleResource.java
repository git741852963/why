package com.neusoft.dxz.module.system.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色权限关联Model
 *
 * @author andy.jiao@msn.com
 */
@Getter
@Setter
@ToString
public class RoleResource extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 所属角色ID
     */
    private Long roleId;
    /**
     * 所属权限ID
     */
    private Long resourceId;
    /**
     * 所属角色信息
     */
    private Role role;
    /**
     * 所属角色权限信息
     */
    private Resource resource;
}
