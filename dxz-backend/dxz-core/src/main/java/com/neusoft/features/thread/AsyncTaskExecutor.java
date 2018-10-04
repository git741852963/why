package com.neusoft.features.thread;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 异步任务调度。
 *
 * @author andy.jiao@msn.com
 */
@Component
@Getter
@Setter
public class AsyncTaskExecutor {

    @Autowired
    private ThreadPoolTaskExecutor threadPool;

    public void execute(final AsyncTask task) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                task.execute();
            }
        });
    }
}
