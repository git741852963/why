package com.neusoft.features.session;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * 分布式session。
 */
public class DistributedSession implements HttpSession {
    private final DistributedSessionManager sessionManager;
    private final String id;
    private final long createdAt;
    private final HttpServletRequest request;
    private final Map<String, Object> newAttributes = Maps.newHashMapWithExpectedSize(5);
    private final Set<String> deleteAttribute = Sets.newHashSetWithExpectedSize(5);
    private final Map<String, Object> dbSession;
    private volatile long lastAccessedAt;
    private int maxInactiveInterval;
    private volatile boolean invalid;
    private volatile boolean dirty;

    public DistributedSession(DistributedSessionManager sessionManager, HttpServletRequest request, String id) {
        this.request = request;
        this.sessionManager = sessionManager;
        this.id = id;
        this.dbSession = loadDBSession();
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = this.createdAt;
    }

    private Map<String, Object> loadDBSession() {
        return this.sessionManager.findSessionById(this.id);
    }

    public long getCreationTime() {
        return this.createdAt;
    }

    public String getId() {
        return this.id;
    }

    public long getLastAccessedTime() {
        return this.lastAccessedAt;
    }

    public ServletContext getServletContext() {
        return this.request.getServletContext();
    }

    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    /**
     * @deprecated
     */
    public HttpSessionContext getSessionContext() {
        return null;
    }

    public Object getAttribute(String name) {
        checkValid();
        if (this.newAttributes.containsKey(name)) {
            return this.newAttributes.get(name);
        }
        if (this.deleteAttribute.contains(name)) {
            return null;
        }
        return this.dbSession.get(name);
    }

    /**
     * @deprecated
     */
    public Object getValue(String name) {
        return getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        checkValid();
        Set<String> names = Sets.newHashSet(this.dbSession.keySet());
        names.addAll(this.newAttributes.keySet());
        names.removeAll(this.deleteAttribute);
        return Collections.enumeration(names);
    }

    /**
     * @deprecated
     */
    public String[] getValueNames() {
        checkValid();
        Set<String> names = Sets.newHashSet(this.dbSession.keySet());
        names.addAll(this.newAttributes.keySet());
        names.removeAll(this.deleteAttribute);
        return names.toArray(new String[0]);
    }

    public void setAttribute(String name, Object value) {
        checkValid();
        if (value != null) {
            this.newAttributes.put(name, value);
            this.deleteAttribute.remove(name);
        } else {
            this.deleteAttribute.add(name);
            this.newAttributes.remove(name);
        }
        this.dirty = true;
    }

    /**
     * @deprecated
     */
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        checkValid();
        this.deleteAttribute.add(name);
        this.newAttributes.remove(name);
        this.dirty = true;
    }

    /**
     * @deprecated
     */
    public void removeValue(String name) {
        removeAttribute(name);
        this.dirty = true;
    }

    public void invalidate() {
        this.invalid = true;
        this.dirty = true;
        this.sessionManager.deletePhysically(getId());
    }

    public boolean isNew() {
        return true;
    }

    protected void checkValid() throws IllegalStateException {
        Preconditions.checkState(!this.invalid);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> snap = Maps.newHashMap();
        snap.putAll(this.dbSession);
        snap.putAll(this.newAttributes);
        for (String name : this.deleteAttribute) {
            snap.remove(name);
        }
        return snap;
    }

    public boolean isValid() {
        return !this.invalid;
    }
}
