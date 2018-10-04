package com.neusoft.features.mail.task;

import com.neusoft.features.mail.MailObject;
import com.neusoft.features.mail.MailSenderFactory;
import com.neusoft.features.thread.AsyncTask;

/**
 * MailSendTask
 *
 * @author andy.jiao@msn.com
 */
public class MailSendTask implements AsyncTask {

    private MailSenderFactory mailSenderFactory;

    private MailObject mail;

    public MailSendTask(MailSenderFactory mailSenderFactory, MailObject mail) {
        this.mailSenderFactory = mailSenderFactory;
        this.mail = mail;
    }

    @Override
    public void execute() {
        mailSenderFactory.getSender().send(mail);
    }
}
