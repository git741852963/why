package com.neusoft.features.common.utils;

import com.google.common.base.Strings;

import java.text.DecimalFormat;


public class NumberUtils {
    private static final DecimalFormat nf = new DecimalFormat("0");
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static String formatNumber(String price) {
        if (Strings.isNullOrEmpty(price)) {
            return "0";
        }
        if (price.startsWith("0")) {
            // 将字符串转化成字符数组
            char strs[] = price.toCharArray();
            int begin = 0;
            for (int i = 0; i < strs.length; i++) {
                if ('0' != strs[i]) {
                    // 找到非零字符串并跳出
                    begin = i;
                    break;
                }
            }
            price = price.substring(begin);
        }
        double _price = 0D;
        try {
            _price = Double.valueOf(price);
        } catch (NumberFormatException e) {
            // IGNORE
        }
        return nf.format(_price);
    }

    public static String formatNumber(Number price) {
        if (price == null) {
            return "0";
        }
        return nf.format(price.doubleValue());
    }

    public static String formatPrice(String price) {
        if (Strings.isNullOrEmpty(price)) {
            return "";
        }
        if (price.startsWith("0")) {
            // 将字符串转化成字符数组
            char strs[] = price.toCharArray();
            int begin = 0;
            for (int i = 0; i < strs.length; i++) {
                if ('0' != strs[i]) {
                    // 找到非零字符串并跳出
                    begin = i;
                    break;
                }
            }
            price = price.substring(begin);
        }
        double _price = 0D;
        try {
            _price = Double.valueOf(price);
        } catch (NumberFormatException e) {
            // IGNORE
        }
        return df.format(_price / 100.0D);
    }

    public static String formatPrice(Number price) {
        if (price == null) {
            return "";
        }
        return df.format(price.doubleValue() / 100.0D);
    }
}
