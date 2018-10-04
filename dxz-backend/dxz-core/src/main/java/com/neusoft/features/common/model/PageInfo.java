package com.neusoft.features.common.model;

/**
 * 分页Helper。
 * <p/>
 * mybatis-spring-1.2.0 中取消了自动注入 SqlSessionFactory 和 SqlSessionTemplate，需要手动注入。
 *
 * @author andy.jiao@msn.com
 */
public class PageInfo {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MIN_PAGE_NO = 1;
    public int offset;
    public int limit;

    public PageInfo(Integer pageNo, Integer size) {
        pageNo = (pageNo == null || pageNo < 1) ? MIN_PAGE_NO : pageNo;
        size = (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size;
        this.limit = size;
        double p = Integer.MAX_VALUE / size;
        if (p < pageNo) {
            throw new RuntimeException("too large page no and page size:" + pageNo + ", " + size);
        }
        this.offset = (pageNo - 1) * size;
        this.offset = this.offset > 0 ? this.offset : 0;
    }

    public String toString() {
        return "PageInfo (offset=" + getOffset() + ", limit=" + getLimit() + ")";
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLimit() {
        return this.limit;
    }
}