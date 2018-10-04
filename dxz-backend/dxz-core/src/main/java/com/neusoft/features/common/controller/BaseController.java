package com.neusoft.features.common.controller;

import com.neusoft.features.common.ErrorCodeConstants;
import com.neusoft.features.common.MessageSources;
import com.neusoft.features.common.enums.FileType;
import com.neusoft.features.common.model.ApiResponse;
import com.neusoft.features.common.model.FileModel;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.utils.FileUtil;
import com.neusoft.features.thread.carrier.FileCreateTask;
import com.neusoft.features.user.base.UserUtil;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * BaseController
 *
 * @author andy.jiao@msn.com
 */
public class BaseController extends AbstractController {

    /** i18n 消息源 */
    @Autowired(required = false)
    protected MessageSources messageSources;

    /**
     * 将response包装为ApiResponse并返回。
     *
     * @param response Response对象
     */
    protected <T> ApiResponse<T> jsonResponse(Response<T> response, String operation) {
        return jsonResponse(response, operation, "");
    }

    /**
     * 将response包装为ApiResponse并返回。
     *
     * @param response Response对象
     * @param operation 操作名称
     */
    protected <T> ApiResponse<T> jsonResponse(Response<T> response, String operation, String successMsg) {
        return jsonResponse(response, operation, successMsg, new Object[0]);
    }

    /**
     * 将response包装为ApiResponse并返回。
     *
     * @param response Response对象
     * @param operation 操作名称
     * @param args 消息参数
     */
    protected <T> ApiResponse<T> jsonResponse(Response<T> response, String operation, String successMsg, Object[] args) {
        ApiResponse<T> wrapper = new ApiResponse<>();

        // 获取操作名i18n字符串
        String i18nOpName = messageSources.get(operation);

        String i18nMessage;
        if (response.isSuccess()) {
            // 操作成功
            wrapper.setResult(response.getResult());
            // 成功消息
            i18nMessage = Strings.isNullOrEmpty(successMsg) ? messageSources.get("common.operation.success", i18nOpName) : messageSources.get(successMsg, args);
            wrapper.setMessage(i18nMessage);
        } else {
            // 操作失败
            i18nMessage = messageSources.get("common.operation.fail", i18nOpName);
            String i18nError = messageSources.get(response.getError());
            wrapper.setError(i18nMessage + ":" + i18nError);
            wrapper.setCode(response.getCode() == null ? ErrorCodeConstants.INTERNAL_ERROR : response.getCode());
            log.error("[*] controller business fail: operation={}, code={}, error={}", operation, response.getCode(), i18nMessage);
        }

        return wrapper;
    }

    /**
     * 将对象包装为ApiResponse并返回成功应答。
     *
     * @param object 包装数据待
     * @param operation 操作名称
     */
    protected <T> ApiResponse<T> success(T object, String operation) {
        return success(object, operation, "");
    }

    /**
     * 将对象包装为ApiResponse并返回成功应答。
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param successMsg 成功消息
     */
    protected <T> ApiResponse<T> success(T object, String operation, String successMsg) {
        return success(object, operation, successMsg, new Object[0]);
    }

    /**
     * 将对象包装为ApiResponse并返回成功应答。
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param successMsg 成功消息
     * @param args 消息参数
     */
    protected <T> ApiResponse<T> success(T object, String operation, String successMsg, Object[] args) {
        String i18nMessage = Strings.isNullOrEmpty(successMsg) ? messageSources.get("common.operation.success", messageSources.get(operation)) : messageSources.get(successMsg, args);

        ApiResponse<T> wrapper = new ApiResponse<>();
        wrapper.setResult(object);
        wrapper.setMessage(i18nMessage);
        return wrapper;
    }

    /**
     * 将对象包装为ApiResponse并返回失败应答。
     *
     * @param operation 操作名称
     */
    protected <T> ApiResponse<T> fail(String operation) {
        return fail(operation, "");
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     */
    protected <T> ApiResponse<T> fail(String operation, String failMsg) {
        return fail(operation, failMsg, new Object[0], null);
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param code 错误代码
     */
    protected <T> ApiResponse<T> fail(String operation, String failMsg, String code) {
        return fail(operation, failMsg, new Object[0], code);
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param args 消息参数
     */
    protected <T> ApiResponse<T> fail(String operation, String failMsg, Object[] args) {
        return fail(operation, failMsg, args, null);
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param args 消息参数
     * @param code 错误码
     */
    protected <T> ApiResponse<T> fail(String operation, String failMsg, Object[] args, String code) {
        String i18nMessage = Strings.isNullOrEmpty(failMsg) ? messageSources.get("common.operation.fail", messageSources.get(operation)) : messageSources.get(failMsg, args);

        ApiResponse<T> wrapper = new ApiResponse<>();
        wrapper.setError(i18nMessage);
        if (!Strings.isNullOrEmpty(code)) {
            wrapper.setCode(code);
        }
        log.error("[*] controller business fail: operation={}, code={}, error={}", operation, wrapper.getCode(), i18nMessage);
        return wrapper;
    }

    /**
     * form表单产出
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @return form submit result
     */
    protected <T> ModelAndView formSuccess(T object, String operation) {
        return formSuccess(object, operation, "");
    }

    /**
     * form表单产出
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param msg 成功消息
     * @return form submit result
     */
    protected <T> ModelAndView formSuccess(T object, String operation, String msg) {
        return formSuccess(object, operation, msg, new Object[0]);
    }

    /**
     * form表单产出
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param msg 成功消息
     * @param msgArgs 消息参数
     * @return form submit result
     */
    protected <T> ModelAndView formSuccess(T object, String operation, String msg, Object[] msgArgs) {
        Response<T> response = new Response<>();
        response.setResult(object);
        return formResponse(response, operation, msg, msgArgs);
    }

    /**
     * 将对象包装为ApiResponse并返回失败应答。
     *
     * @param operation 操作名称
     */
    protected <T> ModelAndView formFail(String operation) {
        return formFail(operation, "");
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     */
    protected <T> ModelAndView formFail(String operation, String failMsg) {
        return formFail(operation, failMsg, new Object[0]);
    }

    /**
     * 将对象包装为ApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param args 消息参数
     */
    protected <T> ModelAndView formFail(String operation, String failMsg, Object[] args) {
        String i18nMessage = Strings.isNullOrEmpty(failMsg) ? messageSources.get("common.operation.fail", operation) : messageSources.get(failMsg, args);

        ApiResponse<T> wrapper = new ApiResponse<>();
        wrapper.setError(i18nMessage);
        log.error("[*] controller business fail: operation={}, code={}, error={}", operation, wrapper.getCode(), i18nMessage);
        return new ModelAndView("resource:/httpform-transfer", "response", wrapper);
    }

    /**
     * form表单产出
     *
     * @param response response object
     * @param operation 操作名称
     * @return form submit result
     */
    protected ModelAndView formResponse(Response response, String operation) {
        return formResponse(response, operation, "");
    }


    /**
     * form表单产出
     *
     * @param response response object
     * @param operation 操作名称
     * @return form submit result
     */
    protected ModelAndView formResponse(Response response, String operation, String message) {
        return formResponse(response, operation, message, new Object[0]);
    }

    /**
     * form表单产出
     *
     * @param response response object
     * @param operation 操作名称
     * @return form submit result
     */
    protected ModelAndView formResponse(Response response, String operation, String message, Object[] args) {
        // 获取操作名i18n字符串
        String i18nOpName = messageSources.get(operation);

        String i18nMessage;
        if (response.isSuccess()) {
            // 成功消息
            i18nMessage = Strings.isNullOrEmpty(message) ? messageSources.get("common.operation.success", i18nOpName) : messageSources.get(message, args);
            response.setMessage(i18nMessage);
        } else {
            // 操作失败
            i18nMessage = messageSources.get("common.operation.fail", i18nOpName);
            String i18nError = messageSources.get(response.getError(), response.getMessageArgs());
            response.setError(i18nMessage + ":" + i18nError);
            response.setCode(response.getCode() == null ? ErrorCodeConstants.INTERNAL_ERROR : response.getCode());
            log.error("[*] controller business fail: operation={}, code={}, error={}", operation, response.getCode(), i18nMessage);
        }
        return new ModelAndView("resource:/httpform-transfer", "response", response);
    }

    /**
     * 上传图片
     *
     * @param multipartFile 图片文件信息
     * @return 图片Model
     * @throws IOException 异常信息
     */
    protected FileModel uploadImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.getSize() <= 0) {
            return null;
        }

        // 获取原始文件名
        String original = multipartFile.getOriginalFilename();

        // 获取文件扩展名
//        String originalExt = original.contains(".") ? original.substring(original.lastIndexOf(".") + 1) : "";
        String originalExt = Files.getFileExtension(original);

        String fileName = FileUtil.genFileName(originalExt);
        String fileLink = FileUtil.genFileUrl(FileType.IMAGE, fileName);
        String filePath = FileUtil.genFilePath(FileType.IMAGE, fileName);

        // 文件信息
        FileModel fileModel = new FileModel();
        fileModel.setUserId(UserUtil.getUserId());
        fileModel.setFileCategory(FileType.IMAGE.value());
        fileModel.setFileOriginalName(original);
        fileModel.setFileExtension(originalExt);
        fileModel.setFileSize(multipartFile.getSize());
        fileModel.setFilePath(fileLink);

        //TODO:这里写的太丑要优化，分线程去做也没什么意义，有时间改成同步的，另外没有支持一次上传多张图片。
        // 保存文件
        FileCreateTask task = new FileCreateTask(multipartFile.getInputStream(), filePath, fileName);
        task.execute();

        return fileModel;
    }
}
