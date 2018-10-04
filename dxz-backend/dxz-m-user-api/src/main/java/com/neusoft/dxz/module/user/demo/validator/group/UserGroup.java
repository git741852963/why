package com.neusoft.dxz.module.user.demo.validator.group;

import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;

/**
 * Resource验证分组，CRUD。
 *
 * @author andy.jiao@msn.com
 */
public interface UserGroup extends BaseValidatorGroup {

    public static interface REGISTER{}

    public static interface CONSOLE_CREATE {}

    public static interface CONSOLE_UPDATE {}

    public static interface CHANGE_PASSWORD {}

}
