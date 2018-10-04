package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.user.dao.UserDao;
import com.neusoft.dxz.module.user.demo.enums.UserStatus;
import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.neusoft.features.common.validator.CommonRegex;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * User Service
 *
 * @author andy.jiao@msn.com
 */
@Service
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleService userRoleService;

    private final static HashFunction sha512 = Hashing.sha512();

    private final static Joiner joiner = Joiner.on('@').skipNulls();

    private final static HashFunction md5 = Hashing.md5();

    private final static Splitter splitter = Splitter.on('@').trimResults();

    private Pattern regExpPhone = Pattern.compile(CommonRegex.USER_PHONE);

    @Override
    public Response<Boolean> register(User user) {
        return null;
    }

    @Override
    public Response<User> login(String name, String password) {

        Response<User> response = new Response<>();

        try {
            // 参数检查
            checkArgument(!Strings.isNullOrEmpty(name), "user.user.param.name.null");
            checkArgument(!Strings.isNullOrEmpty(password), "user.user.param.pwd.null");

            // 根据用户名查找用户
            User user = userDao.findByName(name);
            checkState(user != null, "user.user.login.user.or.pwd.invalid");

            // 判断密码是否相等
            checkState(isPasswordMatch(password, user.getPassword()), "user.user.login.user.or.pwd.invalid");

            // 检查用户状态
            checkState(Objects.equals(user.getStatus(), UserStatus.NORMAL.value()), "user.user.login.user.frozen");

            // 确保密码不回显
            user.setPassword(null);







            // 查找用户角色
            Response<List<Long>> rolesResponse = userRoleService.findRoleIdsByUserId(user.getId());
            checkState(rolesResponse.isSuccess(), rolesResponse.getError());
            user.setRoles(rolesResponse.getResult());

            response.setResult(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to do login, name={}, cause:{}", name, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to do login, name={}, cause:{}", name, Throwables.getStackTraceAsString(e));
            response.setError("user.user.login.fail");
        }

        return response;
    }

    /**
     * 判断密码是否相符
     *
     * @param password 用户输入密码
     * @param encryptedPassword 加密密码
     * @return 密码是否相符
     */
    private boolean isPasswordMatch(String password, String encryptedPassword) {
        Iterable<String> parts = splitter.split(encryptedPassword);
        String salt = Iterables.get(parts, 0);
        String realPassword = Iterables.get(parts, 1);
        String valPassword = sha512.hashUnencodedChars(password + salt).toString().substring(0, 20);
        return Objects.equals(valPassword, realPassword);
    }

    /**
     * 密码加密。
     *
     * 首先使用uuid + 时间戳进行hash，获取前4位作为salt，
     * 再将密码和salt连接进行第二次hash，获取前20位得到
     * 加密密文，将salt和密文用@连接后作为密码保存到DB。
     *
     * @param password 用户输入密码
     * @return 加密密码
     */
    private String encryptPassword(String password) {
        String salt = md5.newHasher().putUnencodedChars(UUID.randomUUID().toString()).putLong(System.currentTimeMillis()).hash()
                         .toString().substring(0, 4);
        String realPassword = sha512.hashUnencodedChars(password + salt).toString().substring(0, 20);
        return joiner.join(salt, realPassword);
    }

    @Override
    public Response<Boolean> isMobileRegistered(String mobile) {

        Response<Boolean> response = new Response<>();

        try {
            checkArgument(!Strings.isNullOrEmpty(mobile), "user.user.param.mobile.null");

            User user = userDao.findByMobile(mobile);
            response.setResult(user != null);
        } catch (IllegalArgumentException e) {
            log.error("failed to find user by mobile, mobile={}, cause:{}", mobile, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find user by mobile, mobile={}, cause:{}", mobile, Throwables.getStackTraceAsString(e));
            response.setError("user.user.find.by.mobile.fail");
        }

        return response;
    }

    @Override
    public Response<User> findById(Long id) {

        Response<User> response = new Response<>();

        try {
            checkArgument(id != null, "user.user.param.id.null");

            // 更新用户
            User user = userDao.findById(id);
            response.setResult(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to find user by id, id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find user by id, id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("user.user.find.by.id.fail");
        }

        return response;
    }

    @Override
    public Response<List<User>> findByIds(List<String> ids) {
        //TODO: not implemented yet
        return null;
    }

    @Override
    public Response<User> findByPhone(String phone) {
        Response<User> response = new Response<>();

        try {
            checkArgument(phone != null, "user.user.param.mobile.null");

            // 根据手机号查找用户
            User user = userDao.findByMobile(phone);
            response.setResult(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to find user by mobile, mobile={}, cause:{}", phone, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find user by mobile, mobile={}, cause:{}", phone, Throwables.getStackTraceAsString(e));
            response.setError("user.user.find.by.mobile.fail");
        }

        return response;
    }

    @Override
    public Response<User> findByName(String name) {
        Response<User> response = new Response<>();

        try {
            checkArgument(name != null, "user.user.param.name.null");

            // 根据手机号查找用户
            User user = userDao.findByName(name);
            response.setResult(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to find user by name, name={}, cause:{}", name, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find user by name, name={}, cause:{}", name, Throwables.getStackTraceAsString(e));
            response.setError("user.user.find.by.name.fail");
        }

        return response;
    }

    @Override
    public Response<CommonDto> find(String userName,
                                    String mobile,
                                    String email,
                                    Integer pageNo,
                                    Integer pageSize) {
        //TODO: not implemented yet
        return null;
    }

    @Override
    public Response<CommonDto<User>> findUser(String mobile, Integer pageNo, Integer pageSize) {

        Response<CommonDto<User>> response = new Response<>();
        CommonDto<User> rtn = new CommonDto<>();

        try {

            // 查找用户
            CommonDto param = new CommonDto();
            param.addFlexibleData("mobile", mobile);
            param.addFlexibleData("type", RoleType.USER.value());

            long count = userDao.appCount(param);
            if (count == 0) {
                response.setResult(rtn);
                return response;
            } else {
                rtn.setTotal(count);
            }

            param.setPageInfo(pageNo, pageSize);
            List<User> users = userDao.appListUser(param);

            rtn.setModelData(users);

            response.setResult(rtn);
        } catch (IllegalStateException e) {
            log.error("failed to find users, mobile={}, category={}, pageNo={}, pageSize={}, cause:{}", mobile,RoleType.USER.value(),pageNo, pageSize, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find users, mobile={}, category={}, pageNo={}, pageSize={}, cause:{}", mobile,RoleType.USER.value(),pageNo, pageSize, Throwables.getStackTraceAsString(
                    e));
            response.setError("user.user.find.users.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> frozen(String ids) {

        Response<Boolean> response = new Response<>();

        try {
            checkArgument(!Strings.isNullOrEmpty(ids), "user.user.param.id.null");

            List<String> idsList = Splitter.on(",").splitToList(ids);

            // 冻结用户
            userDao.frozen(idsList);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to frozen user, ids={}, cause:{}", ids, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to frozen user, ids={}, cause:{}", ids, Throwables.getStackTraceAsString(e));
            response.setError("user.user.frozen.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> unfrozen(String ids) {

        Response<Boolean> response = new Response<>();

        try {
            checkArgument(!Strings.isNullOrEmpty(ids), "user.user.param.id.null");

            List<String> idsList = Splitter.on(",").splitToList(ids);

            // 解冻用户
            userDao.unfrozen(idsList);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to unfrozen user, ids={}, cause:{}", ids, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to unfrozen user, ids={}, cause:{}", ids, Throwables.getStackTraceAsString(e));
            response.setError("user.user.unfrozen.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> writeLoginLog(User user) {
        return null;
    }

    @Override
    public Response<Boolean> changePassword(BaseUser currUser, User user) {
        Response<Boolean> response = new Response<>();

        try {
            checkArgument(currUser != null && currUser.getId() != null, "user.user.current.null");
            checkArgument(user.getId() != null, "user.user.param.id.null");
            checkArgument(user.getId() == currUser.getId(), "user.user.record.not.exist");

            // 密码加密
            user.setPassword(encryptPassword(user.getPassword()));

            // 更新用户密码
            userDao.updatePassword(user);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to change user password, id={}, cause:{}", user.getId(), e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to change user password, id={}, cause:{}", user.getId(), Throwables.getStackTraceAsString(e));
            response.setError("user.user.change.password.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> delete(Long id) {

        Response<Boolean> response = new Response<>();

        try {
            checkArgument(id != null, "user.user.param.id.null");

            // 解冻用户
            userDao.delete(id);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to delete user, id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to delete user, id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("user.user.unfrozen.fail");
        }

        return response;
    }
}
