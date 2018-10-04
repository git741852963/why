package com.neusoft.dxz.web.security;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Andy on 2016/6/27.
 */
public class AuthItem {
    public final Pattern pattern;

    public final Set<Integer> types;

    public final Set<String> roles;

    public AuthItem(Pattern pattern, Set<Integer> types, Set<String> roles) {
        this.pattern = pattern;
        this.types = types;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthItem authItem = (AuthItem) o;

        if (pattern != null ? !pattern.equals(authItem.pattern) : authItem.pattern != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pattern != null ? pattern.hashCode() : 0;
    }
}
