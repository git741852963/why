package com.neusoft.features.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 服务器邮箱登录验证
 *
 * @author andy.jiao@msn.com
 */
@Getter
@Setter
@ToString
public class MailAuthenticator extends Authenticator {

    /**
     * 用户名（登录邮箱）
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 初始化邮箱和密码
     *
     * @param username 邮箱
     * @param password 密码
     */
    public MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}