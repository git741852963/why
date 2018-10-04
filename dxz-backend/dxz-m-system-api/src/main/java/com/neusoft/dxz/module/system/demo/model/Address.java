package com.neusoft.dxz.module.system.demo.model;

import com.neusoft.dxz.module.system.demo.validator.group.ResourceGroup;
import com.neusoft.features.common.model.BaseModel;
import com.neusoft.features.common.validator.CommonRegex;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 地址信息Model<br>
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Address extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 地区编码（国标)
     */
    @NotNull(groups = {ResourceGroup.UPDATE.class}, message = "system.address.param.id.null")
    protected Long id;

    /**
     * 父节点地区编码（国标）
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.address.param.parent.null")
    private Long parentId;

    /**
     * 地区名称
     */
    @NotEmpty(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.name.null")
    @Pattern(regexp = CommonRegex.SYSTEM_ADDRESS_NAME, groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.name.invalid")
    private String name;

    /**
     * 地区层级
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.level.null")
    private Integer level;

    /**
     * 排序
     */
    @NotNull(groups = {ResourceGroup.CREATE.class, ResourceGroup.UPDATE.class}, message = "system.resource.param.sort.null")
    private Long sort;
}
