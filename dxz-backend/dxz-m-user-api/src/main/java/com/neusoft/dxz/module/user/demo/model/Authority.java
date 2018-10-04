package com.neusoft.dxz.module.user.demo.model;

import com.neusoft.features.common.model.BaseModel;
import com.google.common.base.Strings;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限Model
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Authority extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 权限名
     */
    private String name;

    /**
     * 权限URI
     */
    private String uri;

    /**
     * 权限分类
     */
    private int category;

    /**
     * 权限分级
     */
    private int level;

    /**
     * 权限排序
     */
    private int display;

    /**
     * 权限父节点ID
     */
    private String parentId;

    /**
     * 权限是否为叶子
     */
    private boolean isLeaf;

    /**
     * 权限图标
     */
    private String avatar;

    /**
     * 菜单中是否显示
     */
    private boolean sidebarShow;

    /**
     * 显示父节点ID
     */
    private String showParentNodeId;

    /**
     * 显示ID
     */
    private String showNodeId;
    /**
     * 显示PATH
     */
    private String showNodePath;

    /**
     * 权限PATHS集合
     */
    private List<String> paths;

    /**
     * 子权限集合
     */
    private List<Authority> children = new ArrayList<Authority>();

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
            paths = new ArrayList<String>();
        }
        this.paths.add(path);
    }

    /**
     * 设置子权限集合
     *
     * @param children 子权限集合
     */
    public void setChildren(List<Authority> children) {
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
    public void addChild(Authority child) {
        if (this.children.isEmpty()) {
            child.addPath(child.getShowNodePath());
            this.children.add(child);
        } else {
            Authority current = null;
            for (Authority item : this.children) {
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
