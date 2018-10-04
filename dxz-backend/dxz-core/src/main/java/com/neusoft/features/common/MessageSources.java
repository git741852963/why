package com.neusoft.features.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSources implements MessageSource {
    public static final Locale DEFAULT_LOCALE = Locale.CHINA;
    private final MessageSource messageSource;

    @Autowired(required = false)
    public MessageSources(MessageSource messageSource) {
        if (messageSource == null) {
            this.messageSource = new MockMessageSource();
        } else {
            this.messageSource = messageSource;
        }
    }

    public String get(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, new Object[0], code, locale);
    }

    public String get(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, code, locale);
    }

//    public String get(String code) {
//        return this.messageSource.getMessage(code, new Object[0], DEFAULT_LOCALE);
//    }
//
//    public String get(String code, Object... args) {
//        return this.messageSource.getMessage(code, args, DEFAULT_LOCALE);
//    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return this.messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale)
            throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }

    private class MockMessageSource implements MessageSource {
        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return String.format(locale, code, args, defaultMessage);
        }

        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            return String.format(locale, code, args);
        }

        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            return String.format(locale, resolvable.getCodes()[0], resolvable.getArguments(), resolvable.getDefaultMessage());
        }
    }
}