package com.neusoft.dxz.web.security;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Andy on 2016/6/27.
 */
public interface AuthProvider {

    Set<WhiteItem> getWhiteList() throws IOException;

    Set<AuthItem> getProtectList() throws IOException;
}
