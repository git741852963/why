package com.neusoft.dxz.web;

import com.neusoft.dxz.web.exception.NotFound404Exception;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 根据requestURI返回渲染后的view
 */
@Controller
public class RenderMappingHandler {

    /**
     * 渲染页面
     *
     * @param domain   domain
     * @param request  request
     * @param response response
     * @param context  context
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public void path(@RequestHeader("Host") String domain, HttpServletRequest request, HttpServletResponse response, Map<String, Object> context) {

        String path = request.getRequestURI().substring(request.getContextPath().length());
        throw new NotFound404Exception("api not found, path={" + path + "}");

//        String suffix = "";
//        int lastDot = path.lastIndexOf(".");
//        if (lastDot > 0) {
//            suffix = path.substring(lastDot + 1);
//        }
//
//        // 如果在这里api请求如果还没被处理，抛出异常
//        if (path.startsWith("/api/") || ControllerSuffix.from(suffix) != null) {
//            throw new NotFound404Exception("api not found, path={" + path + "}");
//        }
    }
}
