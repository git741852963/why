package com.neusoft.features.user.base;

import java.util.Collections;

public final class UserUtil {
    private static ThreadLocal<BaseUser> user = new ThreadLocal();
    private static ThreadLocal<InnerCookie> cookies = new ThreadLocal() {
        protected InnerCookie initialValue() {
            return new InnerCookie(Collections.<String, String>emptyMap());
        }
    };

    public static void putCurrentUser(BaseUser baseUser) {
        user.set(baseUser);
    }

    public static BaseUser getCurrentUser() {
        return (BaseUser) user.get();
    }

    public static void removeUser() {
        user.remove();
    }

    public static Long getUserId() {
        BaseUser baseUser = getCurrentUser();
        return baseUser != null ? baseUser.getId() : null;
    }

    public static Long getMasterId() {
        BaseUser baseUser = getCurrentUser();
        if (baseUser == null) {
            return null;
        }
        Long parentId = baseUser.getParentId();
        if ((parentId == null) || (parentId.longValue() < 0L)) {
            return baseUser.getId();
        }
        return parentId;
    }

    public static void putCookies(InnerCookie innerCookie) {
        cookies.set(innerCookie);
    }

    public static InnerCookie getInnerCookie() {
        return (InnerCookie) cookies.get();
    }

    public static void clearCookies() {
        cookies.remove();
    }
}