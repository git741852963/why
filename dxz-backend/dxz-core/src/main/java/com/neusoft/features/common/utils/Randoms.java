package com.neusoft.features.common.utils;

import com.google.common.base.Preconditions;

import java.util.Random;

/**
 * Created by Andy on 2015/3/25 0025.
 */
public class Randoms {

    private static char[] NUMBERS_AND_STRING = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static char[] NUMBERS = "0123456789".toCharArray();

    public static final String randString(int len) {
        Preconditions.checkArgument(len > 0);

        Random randGen = new Random();
        char[] randBuffer = new char[len];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = NUMBERS_AND_STRING[randGen.nextInt(NUMBERS_AND_STRING.length)];
        }
        return new String(randBuffer);
    }

    public static final String randNumber(int len) {
        Preconditions.checkArgument(len > 0);

        Random randGen = new Random();
        char[] randBuffer = new char[len];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = NUMBERS[randGen.nextInt(NUMBERS.length)];
        }
        return new String(randBuffer);
    }

    public static final String randStringWithPrefix(String prefix, int len) {
        return prefix + randString(len);
    }

    public static final String randStringWithSuffix(String suffix, int len) {
        return randString(len) + suffix;
    }
}
