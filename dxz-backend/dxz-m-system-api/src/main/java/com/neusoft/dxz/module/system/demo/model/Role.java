package com.neusoft.dxz.module.system.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色Model
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Role extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色类型
     */
    private int category;

    /**
     * 当前类型下是否为默认角色
     */
    private boolean isDefault;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色权限关联列表
     */
    private List<RoleResource> roleAuthorities = new ArrayList<>();

    /**
     * 角色权限列表
     */
    private List<Resource> authorities = new ArrayList<>();

    public void setRoleResources(List<RoleResource> roleAuthorities) {
        if (roleAuthorities == null) {
            this.roleAuthorities.clear();
            return;
        }
        this.roleAuthorities = roleAuthorities;
    }

    public void addRoleResource(RoleResource roleResource) {
        this.roleAuthorities.add(roleResource);
    }

    public void setResources(List<Resource> authorities) {
        if (authorities == null) {
            this.authorities.clear();
            return;
        }
        this.authorities = authorities;
    }

    public void addResource(Resource authority) {
        this.authorities.add(authority);
    }
}
