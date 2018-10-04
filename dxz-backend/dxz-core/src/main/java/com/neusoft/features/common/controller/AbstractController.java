package com.neusoft.features.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller基类。
 *
 * @author andy.jiao@msn.com
 */
public abstract class AbstractController {

    /** log */
    protected Logger log = null ;

    public AbstractController() {
        log =  LoggerFactory.getLogger(getClass().getName());
    }
}
