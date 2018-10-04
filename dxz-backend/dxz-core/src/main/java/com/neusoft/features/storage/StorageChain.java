package com.neusoft.features.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageChain {
    private static Logger logger = LoggerFactory.getLogger(StorageChain.class);
    private StorageChain pre;
    private StorageChain next;
    private Storage current;
    private long expiredSeconds = -1L;

    protected Storable get(String key)
            throws Throwable {
        Storable storable = this.current.get(key);
        if (storable == null) {
            if (this.next != null) {
                logger.debug("Object [{}] not found in {},call next!", key, this.current.getClass().getSimpleName());
                storable = this.next.get(key);
            } else {
                return null;
            }
            if (storable != null) {
                this.current.put(storable);
            }
        } else {
            logger.debug("Object [{}] found in {}!", key, this.current.getClass().getSimpleName());
        }
        return storable;
    }

    protected boolean putForward(Storable storable) {
        if (this.pre != null) {
            this.pre.putForward(storable);
        } else {
            logger.debug("Object [{} with key {}] add to storage [{}]", new Object[]{storable.target, storable.key(), this.current.getClass().getSimpleName()});
        }
        return this.current.put(storable);
    }

    protected void remove(String key) {
        if (this.pre != null) {
            this.pre.remove(key);
        } else {
            logger.debug("Object key {} remove from  storage [{}]", key, this.current.getClass().getSimpleName());
        }
        this.current.remove(key);
    }

    protected void setPre(StorageChain pre) {
        this.pre = pre;
    }

    protected void setNext(StorageChain next) {
        this.next = next;
    }

    protected Storage getCurrent() {
        return this.current;
    }

    protected void setCurrent(Storage current) {
        this.current = current;
    }

    protected long getExpiredSeconds() {
        return this.expiredSeconds;
    }

    protected void setExpiredSeconds(long expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
    }
}