package com.neusoft.dxz.web.security;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Andy on 2016/6/27.
 */
public class WhiteItem {
    public final Pattern pattern;

    public final Set<String> httpMethods;

    public WhiteItem(Pattern pattern, Set<String> httpMethods) {
        this.pattern = pattern;
        this.httpMethods = httpMethods;
    }
}
