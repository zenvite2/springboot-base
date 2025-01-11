package com.ptit.zenvite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {
    @Autowired
    MessageSource messageSource;

    /***
     * Get international message
     * @param messageCode
     * @return
     */
    public String getMessage(String messageCode, Object... args) {
        return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
    }
}
