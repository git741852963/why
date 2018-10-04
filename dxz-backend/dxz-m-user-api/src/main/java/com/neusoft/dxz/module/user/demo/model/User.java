package com.neusoft.dxz.module.user.demo.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.user.demo.validator.group.UserGroup;
import com.neusoft.features.common.model.BaseModel;
import com.neusoft.features.common.validator.CommonRegex;
import com.neusoft.features.common.validator.InSet;
import com.neusoft.features.user.base.BaseUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * 用户信息
 *
 * @author andy.jiao@msn.com
 */
@Data
public class User extends BaseModel implements BaseUser {

    private static final long serialVersionUID = 1L;

    /**
     * 父用户ID
     */
    private Long parentId;

    /**
     * 获取父账号ID
     *
     * @return 服账号ID
     */
    public Long getParentId() {
        return (parentId == null || parentId < 0l) ? id : parentId;
    }

    /**
     * 用户昵称
     */
    @Pattern(regexp = CommonRegex.USER_NICK, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.nick.invalid")
    private String nick;
    /**
     * 用户名(暂定与手机相同)，由于画面只传入手机号，所以验证手机号即可
     */
//    @NotEmpty(groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.name.null")
//    @Pattern(regexp = CommonRegex.USER_PHONE, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.name.invalid")
    @NotEmpty(groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.name.null")
    @Pattern(regexp = CommonRegex.USER_NAME, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.name.invalid")
    private String name;
    /**
     * 用户姓名
     */
    private String realName;
    /**
     * 密码
     */
    @JSONField(serialize=false)
    @JsonIgnore
    @NotEmpty(groups = {UserGroup.CONSOLE_CREATE.class}, message = "user.user.param.password.null")
    @Pattern(regexp = CommonRegex.USER_PASSWORD_NULLABLE, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class, UserGroup.CHANGE_PASSWORD.class}, message = "user.user.param.password.invalid")
    private String password;
    /**
     * 电话
     */
//    @NotEmpty(groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.phone.null")
    @Pattern(regexp = CommonRegex.USER_PHONE, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.phone.invalid")
    private String phone;
    /**
     * 邮件
     */
    private String mail;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 身份证
     */
    private String idCardNum;
    /**
     * 性别 0:男, 1:女
     */
    private Integer gender;
    /**
     * 用户角色类型 0:个人用户 999:管理员
     */
    @NotNull(groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.user.type.invalid")
    @InSet(values = {0, 1, 999}, groups = {UserGroup.CONSOLE_CREATE.class, UserGroup.CONSOLE_UPDATE.class}, message = "user.user.param.user.type.invalid")
    private Integer type;

    //TODO:isSuperMan是否被序列化了，待验证
    /**
     * 是否超级管理员
     */
    @JSONField(serialize=false)
    @JsonIgnore
    private Boolean isSuperMan;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 省
     */
    private Long provinceId;
    /**
     * 市
     */
    private Long cityId;
    /**
     * 区县
     */
    private Long regionId;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 子账户(0:不是;1:是)
     */
    private Integer isChild;
    /**
     * 用户状态(0:正常;1:已冻结)
     */
    private Integer status;
    /**
     * buuuid
     */
    private String buuuid;

    // 以下为辅助字段/方法
//    @JSONField(serialize=false)
//    @JsonIgnore
    private List<Long> roles;

    @JSONField(serialize=false)
    @JsonIgnore
    private RoleType roleTypeEnum;

//    @JSONField(serialize=false)
//    @JsonIgnore
    @Override
    public List<Long> getRoles() {
        return roles;
    }

    @JSONField(serialize=false)
    @JsonIgnore
    @Override
    public Boolean isManager() {
        return roleTypeEnum == null ? false : roleTypeEnum.equals(RoleType.MANAGER);
    }

    @Override
    public Boolean isSuperMan() {
        return isSuperMan;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    //TODO:这个set方法没用吧？没必要，setType直接就设定了，还是说从DB获取之后这里没有值？DB是按field用反射还是getter setter？忘了得看看
    public void setRoleTypeEnum(RoleType roleType) {
        this.roleTypeEnum = roleType;
        this.type = roleTypeEnum.value();
    }

    public void setType(Integer type) {
        RoleType roleType = RoleType.from(type);
        checkState(roleType != null, "角色类型不合法");
        this.type = type;
        this.roleTypeEnum = roleType;
    }
}