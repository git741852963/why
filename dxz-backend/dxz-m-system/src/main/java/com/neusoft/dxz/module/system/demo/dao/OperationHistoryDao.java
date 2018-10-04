package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.log.IDBLogWriter;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Operation History Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class OperationHistoryDao extends CommonDao implements IDBLogWriter {

    @Override
    public int writeLog(Date date, String userId, String userName, String operation, String path, String isSuccess, String param, String returnValue) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("date", date);
        params.put("userId", userId);
        params.put("userName", userName);
        params.put("operation", operation);
        params.put("path", path);
        params.put("isSuccess", isSuccess);
        params.put("param", param);
        params.put("returnValue", returnValue);
        return getSqlSession().insert("system.op-history.insert", params);
    }

    @Override
    public int writeLog(Date date, String formattedMessage) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("date", date);
        params.put("returnValue", formattedMessage);
        return getSqlSession().insert("system.op-history.insert-simple", params);
    }

    public long count(CommonDto param) {
        return getSqlSession().selectOne("system.op-history.count", param);
    }

    public List<Map<String, String>> find(CommonDto param) {
        return getSqlSession().selectList("system.op-history.find", param);
    }
}
