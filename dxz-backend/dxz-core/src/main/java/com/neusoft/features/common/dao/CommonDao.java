package com.neusoft.features.common.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import javax.annotation.Resource;

/**
 * 持久化基类。
 * <p/>
 * mybatis-spring-1.2.0 中取消了自动注入 SqlSessionFactory 和 SqlSessionTemplate，需要手动注入。
 *
 * @author andy.jiao@msn.com
 */
public class CommonDao extends SqlSessionDaoSupport {

    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
}
