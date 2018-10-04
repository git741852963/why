package com.neusoft.features.log;

import java.util.Date;

public interface IDBLogWriter {
    int writeLog(Date date, String userId, String userName, String operation, String path, String isSuccess, String param, String result);

    int writeLog(Date date, String formattedMessage);
}
