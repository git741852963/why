package com.neusoft.dxz.web.demo.api.console;

import com.neusoft.dxz.module.system.demo.dao.RoleRedisDao;
import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.dxz.module.user.demo.exception.UnAuthorize401Exception;
import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.dxz.module.user.demo.service.UserService;
import com.neusoft.dxz.web.constants.GlobalVariables;
import com.neusoft.dxz.web.demo.api.dto.Demo01Param;
import com.neusoft.dxz.web.demo.api.dto.Demo02Param;
import com.neusoft.dxz.web.demo.api.dto.DemoInnerParam;
import com.neusoft.features.api.OpenApiResponse;
import com.neusoft.features.api.annotation.ApiVer;
import com.neusoft.features.api.controller.OpenApiController;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.log.annotation.OperationLog;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * 用户api。
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/user")
@ApiVer(min = 1.0)
public class UserController extends OpenApiController {

    @Autowired
    private GlobalVariables globalVariables;

    @Autowired
    private UserService userService;

    /**
     * 管理后台用户登录。
     * <p/>
     * 微直播运营用户输入用户名&固定密码(PWP)进行登录。
     *
     * @param name   登录名
     * @param password 密码
     * @return 用户TOKEN信息
     */
    @OperationLog(value = "管理后台用户登录", rewriteParam = "password", rewriteParamValue = "******")
    @RequestMapping(value = "/admin-login.form", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OpenApiResponse<String> consoleLogin(@RequestParam("name") String name, @RequestParam("password") String password) {

        // 查找用户信息
        Response<User> loginResult = userService.login(name, password);
        checkState(loginResult.isSuccess(), loginResult.getError());

        User user = loginResult.getResult();

        // 验证用户类型，只有管理员角色类型可以登录
        RoleType type = RoleType.from(user.getType());
        if (type != RoleType.MANAGER) {
            throw new UnAuthorize401Exception();
        }

        // 生成token
        DateTime now = new DateTime();
        String t = Jwts.builder()
                .setIssuedAt(now.toDate())
                .setExpiration(now.plusYears(10).toDate())
                .setNotBefore(now.toDate())
                .setAudience(user.getId().toString())
                .setSubject(user.getId().toString())
                .setIssuer("dxz")
                .signWith(SignatureAlgorithm.HS256, globalVariables.getProp("app.auth.secret.key"))
                .compact();

        return success(t, "用户登录");
    }

    /**
     * 第一个Demo
     *
     * @param param Demo01参数
     * @return demo01返回值
     */
    @RequestMapping(value = "/demo01.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OpenApiResponse<Demo01Param> demo01(@RequestBody Demo01Param param) {

        log.info("param.name={}", param.getName());
        log.info("param.password={}", param.getPassword());

        return success(param, "demo01");
    }

    /**
     * 第二个Demo
     *
     * @param param Demo02参数
     * @return demo02返回值
     */
    @RequestMapping(value = "/demo02.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OpenApiResponse<String> demo02(@RequestBody Demo02Param param) {
        log.info("param.name={}", param.getName());
        for (String s : param.getKeys()) {
            log.info("key={}", s);
        }
        for (DemoInnerParam innerParam : param.getInners()) {
            log.info("innerParam={}", innerParam.getInnerName() + ":" + innerParam.getInnerValue());
        }
        return success("成功!", "demo02");
    }

    /**
     * 第三个Demo
     *
     * @param param Demo03参数
     * @return demo03返回值
     */
    @RequestMapping(value = "/demo03.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OpenApiResponse<String> demo03(@RequestBody Map<String, Object> param) {
        log.info("param.name={}", param.get("name"));
        for (String s : (List<String>)param.get("keys")) {
            log.info("key={}", s);
        }
        for (Map innerParam : (List<Map>)param.get("inners")) {
            log.info("innerParam={}", innerParam.get("innerName") + ":" + innerParam.get("innerValue"));
        }

        return success("不建议使用Map接收参数!", "demo03");
    }

    /**
     * 第四个Demo
     *
     * @param param Demo03参数
     * @return demo03返回值
     */
    @RequestMapping(value = "/demo04.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OpenApiResponse<String> demo04(@RequestBody Map<String, Object> param) {
        return fail("demo04");
    }
}