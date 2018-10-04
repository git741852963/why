package com.neusoft.features.mail;

import com.neusoft.features.common.model.BaseModel;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * MailObject
 *
 * @author andy.jiao@msn.com
 */
@Getter
@Setter
@ToString
public class MailObject extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 收件人
     */
    private List<String> recipients = Lists.newArrayList();
    /**
     * 抄送
     */
    private List<String> duplicates = Lists.newArrayList();
    /**
     * 邮件标题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 邮件附件
     */
    private List<String> affixFiles = Lists.newArrayList();

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        if (recipients == null) {
            this.recipients.clear();
            return;
        }
        this.recipients = recipients;
    }

    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    public List<String> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(List<String> duplicates) {
        if (duplicates == null) {
            this.duplicates.clear();
            return;
        }
        this.duplicates = duplicates;
    }

    public void addDuplicate(String duplicate) {
        this.duplicates.add(duplicate);
    }

    public List<String> getAffixFiles() {
        return affixFiles;
    }

    public void setAffixFiles(List<String> affixFiles) {
        if (affixFiles == null) {
            this.affixFiles.clear();
            return;
        }
        this.affixFiles = affixFiles;
    }

    public void addAffixFile(String affixFile) {
        this.affixFiles.add(affixFile);
    }
}
