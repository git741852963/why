package com.neusoft.features.mail;

import com.neusoft.features.mail.exception.MailSendException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * 服务器邮箱登录验证
 *
 * @author andy.jiao@msn.com
 */
public class MailSender {

    // Mail
    static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    static final String MAIL_SMTP_TTLS_ENABLE = "mail.smtp.starttls.enable";
    static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.EnableSSL.enable";
    static final String MAIL_SMTP_SSL_FACTORY_CLASS = "mail.smtp.socketFactory.class";
    static final String MAIL_SMTP_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    static final String MAIL_SMTP_HOST = "mail.smtp.host";
    static final String MAIL_SMTP_PORT = "mail.smtp.port";
    static final String MAIL_SEPAPATOR = "@";
    /**
     * 发送邮件的props文件
     */
    private final transient Properties props = System.getProperties();
    /**
     * 邮件服务器登录验证
     */
    private transient MailAuthenticator authenticator;
    /**
     * 邮箱session
     */
    private transient Session session;
    private String account;

    /**
     * 初始化邮件发送器
     *
     * @param smtpHost SMTP主机地址
     * @param smtpPort SMTP主机端口
     * @param account  发送邮件的用户名(地址)
     * @param password 密码
     */
    public MailSender(final String smtpHost, final String smtpPort, final String account, final String password) {
        this.account = account;
        String username = account;
        //        if (account.contains(MAIL_SEPAPATOR)) {
        //            username = account.split(MAIL_SEPAPATOR)[0];
        //        }
        init(smtpHost, smtpPort, username, password);
    }

    /**
     * 初始化.
     *
     * @param smtpHost SMTP主机地址
     * @param smtpPort SMTP主机端口
     * @param username 发送邮件的用户名(地址)
     * @param password 密码
     */
    private void init(final String smtpHost, final String smtpPort, final String username, final String password) {
        // 初始化props
        props.put(MAIL_SMTP_AUTH, "true");
        //props.put(MAIL_SMTP_TTLS_ENABLE, STRING_TRUE);
        props.put(MAIL_SMTP_SSL_ENABLE, "true");
        props.put(MAIL_SMTP_SSL_FACTORY_CLASS, MAIL_SMTP_SSL_FACTORY);
        props.put(MAIL_SMTP_HOST, smtpHost);
        props.put(MAIL_SMTP_PORT, smtpPort);
        // 验证
        authenticator = new MailAuthenticator(username, password);
        // 创建session
        session = Session.getInstance(props, authenticator);
    }

    /**
     * 发送邮件（支持群发）
     *
     * @param mail 邮件对象
     * @throws Exception
     */
    public void send(MailObject mail) {

        try {
            // 创建mime类型邮件
            final MimeMessage message = new MimeMessage(session);
            // 设置发信人
            message.setFrom(new InternetAddress(this.account));
            // 设置收件人
            int num = mail.getRecipients().size();
            InternetAddress[] addresses = new InternetAddress[num];
            for (int i = 0; i < num; i++) {
                addresses[i] = new InternetAddress(mail.getRecipients().get(i));
            }
            message.setRecipients(MimeMessage.RecipientType.TO, addresses);

            // 设置抄送
            num = mail.getDuplicates().size();
            if (num > 0) {
                addresses = new InternetAddress[num];
                for (int i = 0; i < num; i++) {
                    addresses[i] = new InternetAddress(mail.getDuplicates().get(i));
                }
                message.setRecipients(MimeMessage.RecipientType.CC, addresses);
            }
            // 设置主题
            message.setSubject(mail.getSubject());
            // 设置邮件内容
            MimeMultipart mp = new MimeMultipart();
            BodyPart contentBody = new MimeBodyPart();
            contentBody.setContent(mail.getContent(), "text/html;charset=utf-8");
            mp.addBodyPart(contentBody);

            // 设置附件
            num = mail.getAffixFiles().size();
            if (num > 0) {
                for (String fileName : mail.getAffixFiles()) {
                    BodyPart fileBody = new MimeBodyPart();
                    FileDataSource fileds = new FileDataSource(fileName);
                    fileBody.setDataHandler(new DataHandler(fileds));
                    fileBody.setFileName(fileds.getName());
                    mp.addBodyPart(fileBody);
                }
            }

            message.setContent(mp);
            message.saveChanges();
            // 发送
            Transport.send(message);
        } catch (Exception e) {
            throw new MailSendException("send mail failed.", e);
        }
    }
}
