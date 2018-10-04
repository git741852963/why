package com.neusoft.features.common.utils;

import java.util.Date;

import org.joda.time.DateTime;

public class Dates {
    public static Date startOfDay(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date).withTimeAtStartOfDay().toDate();
    }

    public static Date endOfDay(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date).withTimeAtStartOfDay().plusDays(1).toDate();
    }
}