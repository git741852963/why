package com.neusoft.features.common.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页Model。
 *
 * @author andy.jiao@msn.com
 */
public class Paging<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long total;
    private List<T> data;

    public Paging(Long total, List<T> data) {
        this.data = data;
        this.total = total;
    }

    public static <T> Paging<T> empty(Class<T> clazz) {
        List<T> emptyList = Collections.emptyList();
        return new Paging(Long.valueOf(0L), emptyList);
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}