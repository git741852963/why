package com.neusoft.features.storage;

public abstract interface Storage {
    public abstract Storable get(String paramString);

    public abstract boolean put(Storable paramStorable);

    public abstract void remove(String paramString);
}