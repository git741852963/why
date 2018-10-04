package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;
import com.neusoft.dxz.module.user.dao.UserGradeDao;
import com.neusoft.dxz.module.user.demo.enums.UserGradeStatus;
import com.neusoft.dxz.module.user.demo.model.UserGrade;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * User Grade Service
 *
 * @author andy.jiao@msn.com
 */
@Service
public  class UserGradeServiceImpl extends BaseService implements UserGradeService {

    @Autowired
    private UserGradeDao userGradeDao;

    @Override
    public Response<List<UserGrade>> all() {
        Response<List<UserGrade>> response = new Response<>();
        try {
            List<UserGrade> grades = userGradeDao.all();
            response.setResult(grades);
        } catch (Exception e) {
            log.error("failed to find all user grade, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("user.grade.query.fail");
        }
        return response;
    }

    @Override
    public Response<Boolean> create(UserGrade userGrade) {
        Response<Boolean> response = new Response<>();

        try {
            validate(userGrade, BaseValidatorGroup.CREATE.class);

            UserGrade gradeInfo = userGradeDao.findByName(userGrade.getName());
            checkState(gradeInfo == null, "user.grade.record.already.exist");

            userGradeDao.create(userGrade);
            response.setResult(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to create user grade, grade={}, cause:{}", userGrade, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to create user grade, grade={}, cause:{}", userGrade, Throwables.getStackTraceAsString(e));
            response.setError("user.grade.create.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> update(UserGrade userGrade) {
        Response<Boolean> response = new Response<>();

        try {
            validate(userGrade, BaseValidatorGroup.UPDATE.class);
            checkArgument(userGrade.getId() != null, "user.grade.param.id.null");

            boolean isExist = userGradeDao.isExist(userGrade.getId(), userGrade.getName());
            checkState(!isExist, "user.grade.record.already.exist");

            userGradeDao.update(userGrade);
            response.setResult(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to update user grade, grade={}, cause:{}", userGrade, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to update user grade, grade={}, cause:{}", userGrade, Throwables.getStackTraceAsString(e));
            response.setError("user.grade.update.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> delete(Long id) {
        Response<Boolean> response = new Response<>();

        try {
            checkArgument(id != null, "user.grade.param.id.null");
            userGradeDao.delete(id);
            response.setResult(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to delete user grade, id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to delete user grade, id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("user.grade.delete.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> changeStatus(Long id, UserGradeStatus status) {
        Response<Boolean> response = new Response<>();

        try {
            checkArgument(status != null, "user.grade.param.status.null");
            userGradeDao.changeStatus(id, status.value());
            response.setResult(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to change user grade status, id={}, status={}, cause:{}", id, status.toString(), e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to change user grade status, id={}, status={}, cause:{}", id, status.toString(), Throwables.getStackTraceAsString(e));
            response.setError("user.grade.change.status.fail");
        }

        return response;
    }
}


