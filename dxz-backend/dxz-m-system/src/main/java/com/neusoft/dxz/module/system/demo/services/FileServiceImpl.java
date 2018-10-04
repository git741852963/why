package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.FileDao;
import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;
import com.neusoft.features.common.model.FileModel;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.neusoft.features.common.utils.Arguments.isNull;
import static com.neusoft.features.common.utils.Arguments.notNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * FileServiceImpl
 *
 * @author andy.jiao@msn.com
 */
@Service
@Getter
@Setter
public class FileServiceImpl extends BaseService implements FileService {

    //TODO:验证/消息 未完成
    //TODO:其他模块的验证也要仔细看一下

    @Autowired
    private FileDao fileDao;

    @Transactional
    @Override
    public Response<FileModel> insert(FileModel fileModel) {
        Response<FileModel> response = new Response<>();

        try {
            validate(fileModel, BaseValidatorGroup.CREATE.class);

            if (fileModel == null) {
                response.setError("记录文件信息失败:文件信息不能为空");
                return response;
            }

            if (fileModel.getUserId() == null) {
                response.setError("记录文件信息失败:用户ID不能为空");
                return response;
            }

            if (Strings.isNullOrEmpty(fileModel.getFilePath())) {
                response.setError("记录文件信息失败:文件URL不能为空");
                return response;
            }

            fileDao.insert(fileModel);
            response.setResult(fileModel);
        } catch (Exception e) {
            log.error("failed to insert file info. cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("记录文件信息失败");
        }
        return response;
    }

    @Transactional
    @Override
    public Response<FileModel> update(FileModel fileModel) {
        Response<FileModel> response = new Response<>();
        try {
            validate(fileModel, BaseValidatorGroup.UPDATE.class);
            if (fileModel == null) {
                response.setError("更新文件信息失败:文件信息不能为空");
                return response;
            }

            if (fileModel.getUserId() != null) {
                response.setError("更新文件信息失败:用户ID不能为空");
                return response;
            }

            if (Strings.isNullOrEmpty(fileModel.getFilePath())) {
                response.setError("更新文件信息失败:文件URL不能为空");
                return response;
            }

            fileDao.update(fileModel);
            response.setResult(fileModel);
        } catch (Exception e) {
            log.error("failed to update file info.");
            response.setError("更新文件信息失败");
        }
        return response;
    }

    @Transactional
    @Override
    public Response<Boolean> delete(FileModel fileModel) {
        Response<Boolean> response = new Response<>();

        try {
            validate(fileModel, BaseValidatorGroup.DELETE.class);
            if (fileModel == null) {
                response.setError("删除文件信息失败:文件信息不能为空");
                return response;
            }

            if (fileModel.getId() == null) {
                response.setError("删除文件信息失败:文件ID不能为空");
                return response;
            }

            fileModel.setDelete(Boolean.TRUE);
            fileDao.delete(fileModel);
            response.setResult(true);
        } catch (Exception e) {
            log.error("failed to delete file info.");
            response.setError("删除文件信息失败");
        }

        return response;
    }

    /**
     * 批量删除
     *
     * @param imageIds 图片Id列表
     * @return 成功/失败
     */
    @Override
    public Response<Boolean> batchDelete(BaseUser baseUser, List<String> imageIds) {
        Response<Boolean> response = new Response<>();
        try {
            checkArgument(notNull(imageIds) && !imageIds.isEmpty(), "system.file.param.file.id.null");

            for (String id : imageIds) {
                checkArgument(StringUtils.isNumeric(id), "system.file.param.file.id.invalid");
            }

            CommonDto<FileModel> fileDto = new CommonDto<>();
            fileDto.addFlexibleData("userId", baseUser.getId());
            fileDto.addFlexibleData("imageIds", imageIds);
            fileDto.addFlexibleData("isDelete", Boolean.TRUE);
            fileDto.addFlexibleData("updateAt", new Date());

            fileDao.delete(fileDto);
            response.setResult(true);
        } catch (Exception e) {
            log.error("failed to delete file info.");
            response.setError("删除文件信息失败");
        }

        return response;
    }

    @Override
    public Response<FileModel> selectOne(FileModel fileModel) {
        Response<FileModel> response = new Response<>();
        try {
            if (fileModel == null) {
                response.setError("查询文件信息失败:文件信息不能为空");
                return response;
            }

            if (fileModel.getId() != null) {
                response.setError("查询文件信息失败:文件ID不能为空");
                return response;
            }
            FileModel result = fileDao.findById(fileModel);
            response.setResult(result);
        } catch (Exception e) {
            log.error("failed to get file info.");
            response.setError("查询文件信息失败");
        }

        return response;
    }

    @Override
    public Response<List<FileModel>> selectList(FileModel fileModel) {
        Response<List<FileModel>> response = new Response<>();

        try {
            if (fileModel == null) {
                response.setError("查询文件信息失败:文件信息不能为空");
                return response;
            }

            if (Strings.isNullOrEmpty(String.valueOf(fileModel.getUserId()))) {
                response.setError("查询文件信息失败:用户信息不能为空");
                return response;
            }

            List<FileModel> result = fileDao.findByCondition(fileModel);
            response.setResult(result);
        } catch (Exception e) {
            log.error("failed to get file info.");
            response.setError("查询文件信息失败");
        }

        return response;
    }

    @Override
    public Response<CommonDto<FileModel>> queryUserImages(BaseUser user,
                                                          String category,
                                                          Integer pageNo,
                                                          Integer pageSize) {
        Response<CommonDto<FileModel>> response = new Response<>();
        try {
            pageNo = MoreObjects.firstNonNull(pageNo, 1);
            pageSize = MoreObjects.firstNonNull(pageSize, 10);
            pageSize = pageSize > 0 ? pageSize : 10;
            int offset = (pageNo - 1) * pageSize;
            offset = offset > 0 ? offset : 0;

            CommonDto<FileModel> result = new CommonDto<>();

            long total = fileDao.getCountForQuery(user.getId(), category);
            result.setTotal(total);
            if (total == 0) {
                List<FileModel> emptyList = Collections.emptyList();
                result.setModelData(emptyList);
            } else {
                List<FileModel> fileModelList = fileDao.queryUserImages(user.getId(), category, offset, pageSize);
                result.setModelData(fileModelList);
            }
            response.setResult(result);
        } catch (Exception e) {
            log.error("queryUserImages .failed to get file info.");
            response.setError("查询用户文件信息失败");
        }
        return response;
    }

    @Override
    public Response<CommonDto<FileModel>> queryUserImages(BaseUser user, String category, Integer pageNo, Integer pageSize, String fileName, String startDate, String endDate, String businessId, String modelType) {
        return null;
    }

    @Override
    public Response<Boolean> deleteAll(BaseUser baseUser) {
        Response<Boolean> response = new Response<>();
        try {
            if (isNull(baseUser)) {
                response.setError("删除用户全部图片失败:用户未登录");
            }

            CommonDto<FileModel> fileDto = new CommonDto<>();
            fileDto.addFlexibleData("userId", baseUser.getId());
            fileDto.addFlexibleData("isDelete", Boolean.TRUE);
            fileDto.addFlexibleData("updateAt", new Date());

            fileDao.delete(fileDto);
            response.setResult(true);
        } catch (Exception e) {
            log.error("delete all user images failed.");
            response.setError("查询用户文件信息失败");
        }

        return response;
    }

    /**
     * 根据OSS文件名删除该用户全部图片
     *
     * @param fileServerName
     * @return
     */
    @Override
    public Response<Boolean> deleteByFileName(List<String> fileServerName) {
        Response<Boolean> response = new Response<>();

        try {
//            if (isNull(baseUser)) {
//                response.setError("删除用户全部图片失败:用户未登录");
//            }
            // 本地删除
            CommonDto<FileModel> dto = new CommonDto<>();
//            fileDto.addFlexibleData("userId", baseUser.getId());
            dto.addFlexibleData("fileNames", fileServerName);

            fileDao.deleteByFileName(dto);
            response.setResult(true);
        } catch (Exception e) {
            log.error("delete user images failed, file names={}", StringUtils.join(fileServerName, ","));
            response.setError("删除文件信息失败");
        }

        return response;
    }

    @Override
    public Response<Boolean> updateBusinessId(List<String> fileIds, String id) {
        Response<Boolean> response = new Response<>();

        try {
//            if (isNull(baseUser)) {
//                response.setError("更新业务ID失败:用户未登录");
//            }

//            fileDao.updateBusinessId(baseUser.getId(), fileIds, id);
            fileDao.updateBusinessId(fileIds, id);

            response.setResult(true);
        } catch (Exception e) {
            log.error("update business id failed, fileId id={},  id={}", fileIds, id);
            response.setError("更新业务ID失败");
        }

        return response;
    }

    /**
     * 将回调的参数（播放地址，封面地址），
     * 保存到file表里
     *
     * @param videoInfo
     * @return
     */
    @Override
    public Response<Boolean> updateVideoInfoByVideoId(Map<String, String> videoInfo) {
        Response<Boolean> response = new Response<>();

        try {
            fileDao.updateVideoInfoByVideoId(videoInfo);
            response.setResult(true);
        } catch (Exception e1) {
            log.error("save video info failed to aliyun video info");
            response.setError("保存阿里云回调信息信息失败");
        }

        return response;
    }

    /**
     * 将回调的参数（播放地址）保存到file表里
     *
     * @param videoId 视频ID
     * @param playUrl 视频地址
     * @return
     */
    @Override
    public Response<Long> updateFileUrlByVodId(String videoId, String playUrl) {
        Response<Long> response = new Response<>();

        try {
            long count = fileDao.updateFileUrlByVodId(videoId, playUrl);
            response.setResult(count);
        } catch (Exception e1) {
            log.error("save video info failed to aliyun video playUrl");
            response.setError("保存阿里云回调信息信息失败(播放地址)");
        }

        return response;
    }

    /**
     * 将回调的参数（封面地址）保存到file表里
     *
     * @param videoId  视频ID
     * @param coverUrl 视频地址
     * @return
     */
    @Override
    public Response<Long> updateCoverUrlByVodId(String videoId, String coverUrl) {
        Response<Long> response = new Response<>();

        try {
            long count = fileDao.updateCoverUrlByVodId(videoId, coverUrl);
            response.setResult(count);
        } catch (Exception e1) {
            log.error("save video info failed to aliyun video coverUrl");
            response.setError("保存阿里云回调信息信息失败(封面)");
        }

        return response;
    }
}
