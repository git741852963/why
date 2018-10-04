package com.neusoft.features.mail;

/**
 * 发件箱工厂
 *
 * @author andy.jiao@msn.com
 */
//@Component
public class MailSenderFactory {

    private String smtpHost;
    private String smtpPort;
    private String mailUser;
    private String mailPassword;
    private MailSender sender;

//    @Autowired
    public MailSenderFactory(String smtpHost, String smtpPort, String mailUser, String mailPassword) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.mailUser = mailUser;
        this.mailPassword = mailPassword;
    }

    /**
     * 获取邮件发送对象。
     *
     * @return 邮件发送对象
     */
    public MailSender getSender() {
        if (sender == null) {
            sender = new MailSender(smtpHost, smtpPort, mailUser, mailPassword);
        }
        return sender;
    }
}