package com.neusoft.features.common.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.neusoft.features.common.ErrorCodeConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Response。
 * <p/>
 * 与Controller/Service交互时使用。
 *
 * @author andy.jiao@msn.com
 */
public class Response<T> implements Serializable {
    /**
     * 序列化版本
     */
    private static final long serialVersionUID = 1L;

    /**
     * 成功/失败
     */
    private boolean success;

    /**
     * 结果数据
     */
    private T result;

    /**
     * 错误码
     */
    private String code = ErrorCodeConstants.INTERNAL_ERROR;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 消息参数
     */
    private String[] messageArgs;

    /**
     * 调用是否成功
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * 设置调用成功/失败
     *
     * @param success 成功/失败
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取调用结果
     *
     * @return 调用结果
     */
    public T getResult() {
        return this.result;
    }

    /**
     * 设置调用结果
     *
     * @param result
     */
    public void setResult(T result) {
        this.success = true;
        this.result = result;
        this.code = "OK";
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    @JSONField(name = "message")
    @JsonProperty("message")
    public String getError() {
        return this.message;
    }

    /**
     * 设置错误消息，并将调用结果设置为失败
     *
     * @param error 错误消息
     */
    public void setError(String error) {
        this.success = false;
        this.message = error;
    }

    /**
     * 设置错误消息，并将调用结果设置为失败
     *
     * @param error 错误消息
     * @param args  消息参数
     */
    public void setError(String error, String[] args) {
        this.success = false;
        this.message = error;
        this.messageArgs = args;
    }

    /**
     * 设置返回消息
     *
     * @param message 返回消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 设置返回消息
     *
     * @param message 返回消息
     * @param args    消息参数
     */
    public void setMessage(String message, String[] args) {
        this.message = message;
        this.messageArgs = args;
    }

    /**
     * 获取消息参数
     *
     * @return 消息参数
     */
    public String[] getMessageArgs() {
        return this.messageArgs;
    }

    /**
     * 设置消息参数
     *
     * @param args 消息参数
     */
    public void setMessageArgs(String[] args) {
        this.messageArgs = args;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 转换为json string
     *
     * @return json string
     */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("success", this.success)
                          .add("result", this.result)
                          .add("message", this.message)
                          .add("code", this.code)
                          .add("messageArgs", this.messageArgs)
                          .omitNullValues()
                          .toString();
    }
}