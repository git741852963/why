package com.neusoft.features.dto;

import com.neusoft.features.common.dto.BaseDto;
import com.neusoft.features.common.model.BaseModel;
import com.neusoft.features.common.model.PageInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Data
public class CommonDto<T extends BaseModel> extends BaseDto {

    private static final long serialVersionUID = 1L;

    /** 数据总量 */
    @Getter
    @Setter
    private long total;

    /** 分页信息 */
    @Getter
    @Setter
    private PageInfo pageInfo;

    /** 设置分页信息 */
    public void setPageInfo(Integer pageNo, Integer pageSize) {
        this.pageInfo = new PageInfo(pageNo, pageSize);
    }

    /** model 数据对象 */
    private List<T> modelData;

    /** 其他数据 */
    private HashMap<String, Object> flexibleData;

    /** 获取model数据列表 */
    public List<T> getModelData() {
        if (modelData == null) {
            modelData = Lists.newArrayList();
        }
        return modelData;
    }

    /** 添加model数据 */
    public void addModelData(T model) {
        if (model == null) {
            return;
        }
        this.getModelData().add(model);
    }

    /** 清空model数据 */
    public void clearModelData() {
        this.getModelData().clear();
    }

    /** 获取自定义数据map */
    public HashMap<String, Object> getFlexibleData() {
        if (flexibleData == null) {
            flexibleData = Maps.newHashMap();
        }
        return flexibleData;
    }

    /** 获取自定义数据 */
    public Object getFlexibleData(String key) {
        if (flexibleData == null) {
            flexibleData = Maps.newHashMap();
        }
        return flexibleData.get(key);
    }

    /** 向自定义数据map中添加数据 */
    public void addFlexibleData(String key, Object data) {
        if (Strings.isNullOrEmpty(key) || data == null) {
            return;
        }
        this.getFlexibleData().put(key, data);
    }

    /** 清空自定义数据map */
    public void clearFlexibleData() {
        this.getFlexibleData().clear();
    }
}
