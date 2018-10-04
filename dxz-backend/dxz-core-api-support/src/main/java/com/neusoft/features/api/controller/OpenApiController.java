package com.neusoft.features.api.controller;

import com.neusoft.features.api.OpenApiResponse;
import com.neusoft.features.common.ErrorCodeConstants;
import com.neusoft.features.common.MessageSources;
import com.neusoft.features.common.controller.AbstractController;
import com.neusoft.features.common.enums.FileType;
import com.neusoft.features.common.model.FileModel;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.utils.FileUtil;
import com.neusoft.features.thread.carrier.FileCreateTask;
import com.neusoft.features.user.base.UserUtil;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Open Api Controller
 *
 * @author andy.jiao@msn.com
 */
public abstract class OpenApiController extends AbstractController {

    /** i18n 消息源 */
    @Autowired(required = false)
    protected MessageSources messageSources;

    /**
     * 将response包装为ApiResponse并返回。
     *
     * @param response Response对象
     */
    protected <T> OpenApiResponse<T> jsonResponse(Response<T> response, String operation) {
        return jsonResponse(response, operation, "");
    }

    /**
     * 将response包装为OpenApiResponse并返回。
     *
     * @param response Response对象
     * @param operation 操作名称
     */
    protected <T> OpenApiResponse<T> jsonResponse(Response<T> response, String operation, String successMsg) {
        return jsonResponse(response, operation, successMsg, new Object[0]);
    }

    /**
     * 将response包装为OpenApiResponse并返回。
     *
     * @param response Response对象
     * @param operation 操作名称
     * @param args 消息参数
     */
    protected <T> OpenApiResponse<T> jsonResponse(Response<T> response, String operation, String successMsg, Object[] args) {

        OpenApiResponse<T> wrapper = new OpenApiResponse<>();
        wrapper.setCode(response.getCode());

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
            log.error("[*] api business error: operation={}, code={}, error={}", operation, response.getCode(), i18nMessage);
        }

        wrapper.setMessage(operation);
        return wrapper;
    }

    /**
     * 将对象包装为OpenApiResponse并返回成功应答。
     *
     * @param object 待包装数据
     * @param operation 操作名称
     */
    protected <T> OpenApiResponse<T> success(T object, String operation) {
        return success(object, operation, "");
    }

    /**
     * 将对象包装为OpenApiResponse并返回成功应答。
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param successMsg 成功消息
     */
    protected <T> OpenApiResponse<T> success(T object, String operation, String successMsg) {
        return success(object, operation, successMsg, new Object[0]);
    }

    /**
     * 将对象包装为OpenApiResponse并返回成功应答。
     *
     * @param object 待包装数据
     * @param operation 操作名称
     * @param successMsg 成功消息
     * @param args 消息参数
     */
    protected <T> OpenApiResponse<T> success(T object, String operation, String successMsg, Object[] args) {
        String i18nMessage = Strings.isNullOrEmpty(successMsg) ? messageSources.get( "common.operation.success" , messageSources.get(operation)) : messageSources.get(successMsg, args);

        OpenApiResponse<T> wrapper = new OpenApiResponse<>();
        wrapper.setResult(object);
        wrapper.setMessage(i18nMessage);
        return wrapper;
    }

    /**
     * 将对象包装为OpenApiResponse并返回失败应答。
     *
     * @param operation 操作名称
     */
    protected <T> OpenApiResponse<T> fail(String operation) {
        return fail(operation, null);
    }

    /**
     * 将对象包装为OpenApiResponse并返回失败应答。
     *
     * @param operation 操作名称
     * @param failMsg 错误消息
     */
    protected <T> OpenApiResponse<T> fail(String operation, String failMsg) {
        return fail(operation, failMsg, "");
    }

    /**
     * 将对象包装为OpenApiResponse并返回失败应答。
     *
     * @param operation 操作名称
     * @param failMsg 错误消息
     * @param code 消息代码
     */
    protected <T> OpenApiResponse<T> fail(String operation, String failMsg, String code) {
        return fail(operation, failMsg, null, code);
    }

    /**
     * 将对象包装为OpenApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param args 消息参数
     */
    protected <T> OpenApiResponse<T> fail(String operation, String failMsg, Object[] args) {
        return fail(operation, failMsg, args, null);
    }

    /**
     * 将对象包装为OpenApiResponse并返回。
     *
     * @param operation 操作名称
     * @param failMsg 失败消息
     * @param args 消息参数
     * @param code 错误码
     */
    protected <T> OpenApiResponse<T> fail(String operation, String failMsg, Object[] args, String code) {
        String i18nMessage = Strings.isNullOrEmpty(failMsg) ? messageSources.get("common.operation.fail", messageSources.get(operation)) : messageSources.get(failMsg, args);

        OpenApiResponse<T> wrapper = new OpenApiResponse<>();
        wrapper.setError(i18nMessage);
        if (!Strings.isNullOrEmpty(code)) {
            wrapper.setCode(code);
        }
        log.error("[*] api business error: operation={}, code={}, error={}", operation, wrapper.getCode(), i18nMessage);

        return wrapper;
    }

    /**
     * 上传图片
     *
     * @param base64Img base64编码的图片
     * @return 图片Model
     * @throws java.io.IOException 异常信息
     */
    protected FileModel uploadImage(String base64Img, String originalExt) throws IOException {
        if (base64Img == null || base64Img.length() <= 0) {
            return null;
        }

//        // data:image/png;base64,
//        int sepPos = base64Img.indexOf(",");
//        String prefix = base64Img.substring(0, sepPos - 1);
//        String imgData = base64Img.substring(sepPos + 1);
//
//        Pattern pattern = Pattern.compile("^data:(.*);base64,$");
//        Matcher matcher = pattern.matcher(prefix);
//        boolean isMatched = matcher.matches();
//        String suffixString = "";
//        if (isMatched) {
//            suffixString = matcher.group(0).trim();
//        }
//
//        Base64MediaType fileType = Base64MediaType.from(suffixString);

        // 获取原始文件名
        String original = "upload" + DateTime.now().toString("yyyyMMddHHmmss");

        // 获取文件扩展名
//        String originalExt = fileType == null ? "" : fileType.toString();

        String fileName = FileUtil.genFileName(originalExt);
        String fileLink = FileUtil.genFileUrl(FileType.IMAGE, fileName);
        String filePath = FileUtil.genFilePath(FileType.IMAGE, fileName);

        // base64解码
        byte[] bytes = new BASE64Decoder().decodeBuffer(base64Img);
        //TODO:这段貌似没什么用
        //        for (int i = 0; i < b.length; ++i) {
        //            if (b[i] < 0) {
        //                // 调整异常数据
        //                b[i] += 256;
        //            }
        //        }

        // 文件信息
        FileModel fileModel = new FileModel();
        fileModel.setUserId(UserUtil.getUserId());
        fileModel.setFileCategory(FileType.IMAGE.value());
        fileModel.setFileOriginalName(original);
        fileModel.setFileExtension(originalExt);
        fileModel.setFileSize((long)bytes.length);
        fileModel.setFilePath(fileLink);

        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);

        //TODO:这里写的太丑要优化，分线程去做也没什么意义，有时间改成同步的，另外没有支持一次上传多张图片。
        // 保存文件
        FileCreateTask task = new FileCreateTask(inStream, filePath, fileName);
        task.execute();

        return fileModel;
    }
}
