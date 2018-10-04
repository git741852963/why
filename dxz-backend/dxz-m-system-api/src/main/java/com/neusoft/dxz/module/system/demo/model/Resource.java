package com.neusoft.dxz.module.system.demo.model;

import com.neusoft.dxz.module.system.demo.enums.ResourceType;
import com.neusoft.dxz.module.system.demo.validator.group.ResourceGroup;
import com.neusoft.features.common.model.BaseModel;
import com.neusoft.features.common.validator.InSet;
import com.neusoft.features.common.validator.ValidEnum;
import com.google.common.base.Strings;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统资源<br>
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Resource extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 权限名
     */
    @NotEmpty(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.name.null")
    @Length(min = 1, max = 8, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.name.length")
    private String name;

    /**
     * 权限说明
     */
    @NotEmpty(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.description.null")
    @Length(min = 1, max = 20, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.description.length")
    private String description;

    /**
     * 权限uri
     */
    @Length(min = 1, max = 255, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.uri.length")
    private String uri;

    /**
     * 权限分类
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.category.null")
    @ValidEnum(clazz = ResourceType.class, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message="system.resource.param.category.invalid")
    private Integer category;

    /**
     * 权限分级(0,1)，最多2级
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.level.null")
    @InSet(values = {0, 1}, message="system.resource.param.level.invalid")
    private Integer level;

    /**
     * 权限排序
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.display.null")
    private Integer display;

    /**
     * 权限父节点ID
     */
    private Long parentId;

    /**
     * 权限是否为叶子
     */
    private Boolean isLeaf;

    /**
     * 权限图标
     */
    @Length(min = 0, max = 255, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.avatar.length")
    private String avatar;

    /**
     * 菜单中是否显示
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.sideBar.show.null")
    private Boolean sidebarShow;

    /**
     * 显示父节点id
     */
    private String showParentNodeId;

    /**
     * 显示id
     */
    private Long showNodeId;

    /**
     * 显示path
     */
    private String showNodePath;

    /**
     * 权限paths集合
     */
    private List<String> paths;

    /**
     * 子权限集合
     */
    private List<Resource> children = new ArrayList<>();

    /**
     * 设置子权限PATH集合<br>
     *
     * @param paths 子权限PATH集合
     */
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    /**
     * 添加子权限PATH
     *
     * @param path 子权限 path
     */
    public void addPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return;
        }
        if (this.paths == null) {
            paths = new ArrayList<>();
        }
        this.paths.add(path);
    }

    /**
     * 设置子权限集合
     *
     * @param children 子权限集合
     */
    public void setChildren(List<Resource> children) {
        if (children == null) {
            this.children.clear();
            return;
        }
        this.children = children;
    }

    /**
     * 添加子权限
     *
     * @param child 子权限
     */
    public void addChild(Resource child) {
        if (this.children.isEmpty()) {
            child.addPath(child.getShowNodePath());
            this.children.add(child);
        } else {
            Resource current = null;
            for (Resource item : this.children) {
                if (item.getId().equals(child.getId())) {
                    current = item;
                    break;
                }
            }
            if (current == null) {
                child.addPath(child.getShowNodePath());
                this.children.add(child);
            } else {
                current.addPath(child.getShowNodePath());
            }
        }
    }

    /**
     * 判断是否为叶子
     *
     * @return 是否为叶子 true or false
     */
    public boolean getLeaf() {
        if (this.children == null || this.children.size() == 0) {
            this.isLeaf = true;
        }
        this.isLeaf = false;
        return isLeaf;
    }
}
