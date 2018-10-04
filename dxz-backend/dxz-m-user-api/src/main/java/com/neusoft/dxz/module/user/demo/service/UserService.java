package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;

import java.util.List;

/**
 * 用户信息接口。
 * <p/>
 * 摘要：提供对用户信息的CRUD处理。
 *
 * @author andy.jiao@msn.com
 */
public interface UserService {

    /**
     * 用户注册。
     * <p/>
     * 商城用户注册，如果注册成功，返回true，否则抛出异常。
     * <p/>
     * 以下参数必填：<br>
     * userModel.userName
     * userModel.password
     * <p/>
     * 注意：<br>
     * 1.校验注册用户名字格式。<br>
     * 2.校验注册用户名字，手机，邮箱是否已经存在。<br>
     *
     * @param user 用户注册信息
     * @return 是否成功
     */
    Response<Boolean> register(User user);

    /**
     * 用户登录。
     *
     * @param name     登录名
     * @param password 密码
     * @return 用户信息
     */
    Response<User> login(String name, String password);

    /**
     * 判断电话号码是否已经注册。
     *
     * @param mobile 电话号码
     * @return 是/否
     */
    Response<Boolean> isMobileRegistered(String mobile);

    /**
     * 根据用户id查找用户信息。
     *
     * @param id 用户id
     * @return 用户信息
     */
    Response<User> findById(Long id);

    /**
     * 根据用户ID批量查找用户信息。
     * <p/>
     * 根据传入的用户ID集合参数查询用户信息集合，如果查询成功，返回数据，否则抛出异常。
     *
     * @param ids 用户ID集合
     * @return 用户信息列表
     */
    Response<List<User>> findByIds(List<String> ids);

    /**
     * 根据手机号查找用户信息。
     *
     * @param phone 手机号
     * @return 用户信息
     */
    Response<User> findByPhone(String phone);

    /**
     * 根据用户名查找用户信息。
     *
     * @param name 用户名
     * @return 用户信息
     */
    Response<User> findByName(String name);

    /**
     * 根据检索条件查询用户信息（模糊检索）。
     * <p/>
     * 根据检索条件模糊匹配查询用户信息，如果查询成功，返回数据，否则抛出异常。
     * <p/>
     * 注意：<br>
     * 1.所以参数通过Dto.flexibleData返回前台。<br>
     *
     * @param userName [optional] 用户名
     * @param mobile   [optional] 手机号
     * @param email    [optional] email
     * @param pageNo   [optional] 当前页
     * @param pageSize [optional] 查询记录数
     * @return 用户信息Dto
     */
    Response<CommonDto> find(String userName,
                             String mobile,
                             String email,
                             Integer pageNo,
                             Integer pageSize);

    /**
     * 加载APP用户。
     * <p/>
     * 根据传入的参数加载成员信息集合，如果加载成功，返回数据，否则抛出异常。
     * <p>
     * 对应组件：user管理[console/user/user_management]
     * <p/>
     * 注意：<br>
     * 1.传入的参数通过MemberDto.flexibleData,返回前台。<br>
     *
     * @param mobile   [optional] 电话号码
     * @param pageNo   [optional] 当前页面
     * @param pageSize [optional] 每页显示个数
     * @return 成员信息集合
     */
    Response<CommonDto<User>> findUser(String mobile,
                                       Integer pageNo,
                                       Integer pageSize);

    /**
     * 冻结用户。
     * <p/>
     * 根据传入的参数批量冻结用户。
     *
     * @param ids 用户id列表
     * @return 成功/失败
     */
    Response<Boolean> frozen(String ids);

    /**
     * 解冻用户。
     * <p/>
     * 根据传入的参数批量解冻用户。
     *
     * @param ids 用户id列表
     * @return 成功/失败
     */
    Response<Boolean> unfrozen(String ids);

    /**
     * 更新用户的登录时间、次数、IP地址等信息。
     * <p/>
     * 根据传入的参数更新登录时间、次数、IP地址等信息，同时记录登录日志。
     *
     * @param user 用户信息
     * @return 成功/失败
     */
    Response<Boolean> writeLoginLog(User user);

    /**
     * 修改用户密码。
     *
     * @param currUser 当前用户
     * @param user     用户信息
     * @return 成功/失败
     */
    Response<Boolean> changePassword(BaseUser currUser, User user);

    /**
     * 根据用户ID删除用户。
     *
     * @param id 用户ID
     * @return 成功/失败
     */
    Response<Boolean> delete(Long id);
}
