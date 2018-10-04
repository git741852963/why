package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.OperationHistoryDao;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.enums.OperationResultType;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OperationLogServiceImpl extends BaseService implements OperationLogService {

    @Autowired
    private OperationHistoryDao operationHistoryDao;

    @Override
    public Response<CommonDto> find(String userName, String operation, Integer type, String startAt, String endAt, Integer pageNo, Integer pageSize) {
        Response<CommonDto> response = new Response<>();
        CommonDto rtn = new CommonDto<>();

        try {
            rtn.addFlexibleData("types", OperationResultType.toList());

            // 查找用户
            CommonDto param = new CommonDto();
            param.addFlexibleData("userName", userName);
            param.addFlexibleData("operation", operation);
            param.addFlexibleData("type", type);
            param.addFlexibleData("startAt", startAt);
            if (!Strings.isNullOrEmpty(endAt)) {
                param.addFlexibleData("endAt", endAt + " 23:59:59");
            }

            long count = operationHistoryDao.count(param);
            if (count == 0) {
                response.setResult(rtn);
            } else {
                rtn.setTotal(count);
                param.setPageInfo(pageNo, pageSize);
                List<Map<String, String>> opHistory = operationHistoryDao.find(param);

                rtn.addFlexibleData("logs", opHistory);
                response.setResult(rtn);
            }
            return response;
        } catch (IllegalStateException e) {
            log.error("failed to find operation history, userName={}, startAt={}, endAt={}, operation={}, pageNo={}, pageSize={}, cause:{}", userName, startAt, endAt, operation, pageNo, pageSize, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find operation history, userName={}, startAt={}, endAt={}, operation={}, pageNo={}, pageSize={}, cause:{}", userName, startAt, endAt, operation, pageNo, pageSize, Throwables.getStackTraceAsString(
                    e));
            response.setError("system.op-history.find.fail");
        }

        return response;
    }

}
