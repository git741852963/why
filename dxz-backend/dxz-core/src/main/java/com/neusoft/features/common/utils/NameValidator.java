package com.neusoft.features.common.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Set;
import java.util.regex.Pattern;

public class NameValidator {
    private static final Pattern pattern = Pattern.compile("([a-z]|[A-Z]|[0-9]|[\\u4e00-\\u9fa5]|_)+");
    private static final Pattern digits = Pattern.compile("[0-9]+");
    private static final Splitter DOT_SEP = Splitter.on('.').omitEmptyStrings().trimResults();
    private static final Splitter PATH_SEP = Splitter.on('/').omitEmptyStrings().trimResults();
    private static final Set<String> reservedUserNames = ImmutableSet.<String>builder().add("admin").add("管理员").add("系统管理员").add("ddbao").add("多多宝").add("system").add("administrator").add("rrs").add("日日顺").add("root").build();
    private static final Set<String> reservedSubDomain = ImmutableSet.<String>builder().add("admin").add("design").add("item").add("search").add("static").add("assets").add("news").add("blog").add("support").add("mail").add("forum").add("email").add("group").add("ssl").build();
    private static final Set<String> reservedPath = ImmutableSet.<String>builder().add("design/").add("api/").add("images/").add("oauth/").add("admin/").add("oauth2").build();

    public static boolean validate(String name) {
        return (Strings.isNullOrEmpty(name)) || (pattern.matcher(name).matches());
    }

    public static boolean isAllowedUserName(String name) {
        return !reservedUserNames.contains(name.toLowerCase());
    }

    public static boolean isAllowedSubDomain(String subDomain) {
        if (Strings.isNullOrEmpty(subDomain)) {
            return true;
        }
        String prefix = (String) Iterables.get(DOT_SEP.split(subDomain), 0);
        if (digits.matcher(prefix).matches()) {
            return true;
        }
        if ((prefix.length() < 4) || (prefix.length() > 20)) {
            return false;
        }
        if (!pattern.matcher(prefix).matches()) {
            return false;
        }
        return !reservedSubDomain.contains(prefix.toLowerCase());
    }

    public static boolean isAllowedPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return true;
        }
        String prefix = (String) Iterables.get(PATH_SEP.split(path), 0) + '/';
        return !reservedPath.contains(prefix.toLowerCase());
    }
}