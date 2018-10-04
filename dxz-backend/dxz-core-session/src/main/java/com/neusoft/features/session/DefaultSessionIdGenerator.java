package com.neusoft.features.session;

import com.neusoft.features.session.util.WebUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * session id 生成器。
 * <p/>
 * 1.获取服务器IP或生成UUID，并做hash。
 * 2.生成sessionID：客户端IP hash + SEP + 服务器IP hash + SEP + 当前时间 + SEP + Random(4)。
 *
 * @author andy.jiao@msn.com
 */
public class DefaultSessionIdGenerator implements SessionIdGenerator {
    /**
     * 分隔符
     */
    public static final Character SEP = Character.valueOf('Z');

    /**
     * 客户端IP hash
     */
    private final String hostIpMd5;

    /**
     * 构造函数，获取服务端IP
     */
    public DefaultSessionIdGenerator() {
        String hostIp;
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostIp = UUID.randomUUID().toString();
        }
        this.hostIpMd5 = Hashing.md5().hashString(hostIp, Charsets.UTF_8).toString().substring(0, 8);
    }

    /**
     * 生成session id
     *
     * @param request request object
     * @return session id
     */
    public String generateId(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(30);
        String remoteIpMd5 = Hashing.md5().hashString(WebUtil.getClientIpAddr(request), Charsets.UTF_8).toString().substring(0, 8);
        builder.append(remoteIpMd5).append(SEP).append(this.hostIpMd5).append(SEP).append(Long.toHexString(System.currentTimeMillis())).append(SEP).append(UUID.randomUUID().toString().substring(0, 4));
        return builder.toString();
    }
}
