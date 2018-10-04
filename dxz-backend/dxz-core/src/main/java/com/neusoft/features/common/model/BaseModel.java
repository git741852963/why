package com.neusoft.features.common.model;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.features.common.dto.BaseData;
import com.neusoft.features.db.annotation.GeneratedValue;
import com.neusoft.features.db.annotation.enums.GeneratedItem;

import java.util.Date;

/**
 * BaseModel
 *
 * @author andy.jiao@msn.com
 */
//TODO:这里为了使用JRebel注释了@Data注解，使用Getter/Setter方法，项目完成后换回来。。。
//@Data
public abstract class BaseModel extends BaseData implements Indexable {

    private static final long serialVersionUID = 1L;

    // Object ID
    protected Long id;

    // 创建时间
    @GeneratedValue(generator = GeneratedItem.CREATEAT)
    protected Date createAt;

    // 更新时间
    @GeneratedValue(generator = GeneratedItem.UPDATEAT)
    protected Date updateAt;

    // 是否删除
    @GeneratedValue(generator = GeneratedItem.ISDELETE)
    protected boolean isDelete;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
