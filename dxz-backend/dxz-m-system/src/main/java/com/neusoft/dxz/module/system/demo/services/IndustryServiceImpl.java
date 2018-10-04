package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.IndustryDao;
import com.neusoft.dxz.module.system.demo.model.Industry;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 行业相关Service。
 *
 * @author guozhangjie
 */
@Service
public class IndustryServiceImpl extends BaseService implements IndustryService {

    @Autowired
    private IndustryDao industryDao;

    @Override
    public Response<List<Industry>> all() {
        Response<List<Industry>> response = new Response<>();
        try {
            List<Industry> industries = industryDao.all();
            response.setResult(industries);
        } catch (Exception e){
            log.error("fail to find all industry, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.industry.query.fail");
        }
        return response;
    }

    @Override
    public Response<Industry> findById(Long industryId) {
        Response<Industry> response = new Response<>();
        try {
            Industry industry = industryDao.findById(industryId);
            if (industry == null) {
                response.setError("system.industry.record.not.exist");
            } else {
                response.setResult(industry);
            }
        } catch (Exception e){
            log.error("fail to find all industry, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.industry.query.fail");
        }
        return response;
    }
}
