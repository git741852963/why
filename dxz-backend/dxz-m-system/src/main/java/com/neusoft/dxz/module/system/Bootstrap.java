package com.neusoft.dxz.module.system;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;


public class Bootstrap {

    public static void main(String[] args) throws Exception {

        // load logback configuration
//        LogbackConfigurer.initLogging("classpath:config/logback.xml");

        final ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("/spring/system-root-context.xml");
        ac.start();
        System.out.println("system service started successfully");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown hook was invoked. Shutting down system service.");
                ac.close();
            }
        });

        // prevent main thread from exit
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
