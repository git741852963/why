package com.neusoft.features.common.event;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步消息处理。
 *
 * @author andy.jiao@msn.com
 */
public class AbstractAsyncEventListener {

    /** log */
    protected Logger log = null;

    public AbstractAsyncEventListener() {
        log =  LoggerFactory.getLogger(getClass().getName());
    }

    @Subscribe
    public void handleDeadEvent(DeadEvent event) {
        log.error("dead event, class={}, event={}", event.getSource().getClass(), event.getEvent());
    }
}
