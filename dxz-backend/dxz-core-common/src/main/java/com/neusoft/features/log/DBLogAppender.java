package com.neusoft.features.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.neusoft.features.common.utils.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * logback db appender
 */
@Component
public class DBLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private IDBLogWriter dbLogWriter;
    
    public DBLogAppender(){
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (dbLogWriter == null) {
            dbLogWriter = SpringContextHolder.getBean(IDBLogWriter.class);
        }

        if (dbLogWriter == null) {
            return;
        }

        if (eventObject.getMarker() != null && "ConsoleOperation".equalsIgnoreCase(eventObject.getMarker().getName())) {
            // console operation log
            Object[] params = eventObject.getArgumentArray();
            dbLogWriter.writeLog(new Date(eventObject.getTimeStamp()), (String)params[1], (String)params[2], (String)params[3], (String)params[4], (String)params[5], (String)params[6], (String)params[7]);
        } else {
            // other log
            dbLogWriter.writeLog(new Date(eventObject.getTimeStamp()), eventObject.getFormattedMessage());
        }
    }
}
