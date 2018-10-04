package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.features.common.model.Response;
import com.neusoft.features.dto.CommonDto;

/**
 * 用户操作记录接口。
 *
 * @author andy.jiao@msn.com
 */
public interface OperationLogService {

    /**
     * 根据检索条件查询操作记录信息（模糊检索）。
     * <p/>
     * 注意：<br>
     * 1.所以参数通过Dto.flexibleData返回前台。<br>
     *
     * @param userName  [optional] 用户名
     * @param operation [optional] 操作
     * @param type      [optional] 操作是否成功
     * @param startAt   [optional] 开始时间
     * @param endAt     [optional] 结束时间
     * @param pageNo    [optional] 当前页
     * @param pageSize  [optional] 查询记录数
     * @return 操作记录信息Dto
     */
    Response<CommonDto> find(String userName,
                             String operation,
                             Integer type,
                             String startAt,
                             String endAt,
                             Integer pageNo,
                             Integer pageSize);
}
