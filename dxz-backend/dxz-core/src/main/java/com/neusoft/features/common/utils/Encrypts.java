package com.neusoft.features.common.utils;

import com.google.common.base.Joiner;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.UUID;

/**
 * encrypt util
 *
 * @author andy.jiao@msn.com
 */
public class Encrypts {

    private final static HashFunction md5 = Hashing.md5();

    private final static HashFunction sha512 = Hashing.sha512();

    private final static Joiner joiner = Joiner.on('@').skipNulls();

    /**
     * 密码加密
     *
     * @param str 密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String str) {
        String salt = md5.newHasher()
                         .putUnencodedChars(UUID.randomUUID().toString())
                         .putLong(System.currentTimeMillis()).hash().toString()
                         .substring(0, 8);
        String realPassword = sha512.hashUnencodedChars(str + salt)
                                    .toString().substring(0, 24);
        return joiner.join(salt, realPassword);
    }
}
