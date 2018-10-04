package com.neusoft.features.common.event;

import com.google.common.eventbus.AsyncEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * Event Bus.
 *
 * @author andy.jiao@msn.com
 */
public class AbstractAsyncEventBus {

    /** log */
    protected Logger log = null ;

    /** 异步EventBus */
    private final AsyncEventBus eventBus;

    public AbstractAsyncEventBus() {
        log =  LoggerFactory.getLogger(getClass().getName());
        this.eventBus = new AsyncEventBus(Executors.newFixedThreadPool(4));
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void post(Object event) {
        eventBus.post(event);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }
}
