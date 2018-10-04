package com.neusoft.features.common.utils;

import java.util.List;

public abstract class NumberValidator {
    public static boolean gt0(Long num) {
        return (num != null) && (num.longValue() > 0L);
    }

    public static boolean gt0(List<Long> nums) {
        if ((nums == null) || (nums.size() < 0)) {
            return false;
        }
        for (Long num : nums) {
            if (!gt0(num)) {
                return false;
            }
        }
        return true;
    }

    public static boolean gt0(Integer num) {
        return (num != null) && (num.intValue() > 0);
    }
}
