package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.features.common.model.FileModel;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.dto.CommonDto;
import com.neusoft.features.user.base.BaseUser;

import java.util.List;
import java.util.Map;

/**
 * 文件维护接口。
 * <p/>
 * 摘要：提供对文件系统维护操作。
 *
 * @author andy.jiao@msn.com
 */
public interface FileService {

    /**
     * 文件管理添加文件记录
     * <p/>
     * 文件管理添加文件记录，返回操作应答
     *
     * @param fileModel 文件信息
     * @return 操作应答
     */
    Response<FileModel> insert(FileModel fileModel);

    /**
     * 文件管理更新文件信息
     * <p/>
     * 文件管理更新文件信息，返回操作应答
     *
     * @param fileModel 文件信息
     * @return 操作应答
     */
    Response<FileModel> update(FileModel fileModel);

    /**
     * 文件管理删除文件信息
     * <p/>
     * 文件管理删除文件信息，返回操作应答
     *
     * @param fileModel 文件信息
     * @return 操作应答
     */
    Response<Boolean> delete(FileModel fileModel);

    /**
     * 文件管理批量删除文件信息
     * <p/>
     * 文件管理批量删除文件信息，返回操作应答
     *
     * @param baseUser 当前用户
     * @param imageIds 批量删除文件ID列表
     * @return 操作应答
     */
    Response<Boolean> batchDelete(BaseUser baseUser, List<String> imageIds);

    /**
     * 查找文件信息
     * <p/>
     * 根据文件ID查找文件信息，返回操作应答
     *
     * @param fileModel 文件信息
     * @return 操作应答
     */
    Response<FileModel> selectOne(FileModel fileModel);

    /**
     * 查询文件列表
     * <p/>
     * 根据用户ID查询用户相关的所有文件列表，返回操作应答
     *
     * @param fileModel 文件信息
     * @return 文件列表
     */
    Response<List<FileModel>> selectList(FileModel fileModel);

    /**
     * 用户图片分页查询
     * <p/>
     * 用户图片分页查询，返回操作应答
     *
     * @param user     当前登录用户
     * @param category 文件类型
     * @param pageNo   分页码
     * @param pageSize 分页大小
     * @return FileDto.modelData文件列表
     */
    Response<CommonDto<FileModel>> queryUserImages(BaseUser user, String category, Integer pageNo, Integer pageSize);

    /**
     * 用户图片分页查询
     * <p/>
     * 用户图片分页查询，返回操作应答
     *
     * @param user       当前登录用户
     * @param category   文件类型
     * @param pageNo     分页码
     * @param pageSize   分页大小
     * @param fileName   文件名称
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @param businessId 业务ID
     * @return FileDto.modelData文件列表
     */
    Response<CommonDto<FileModel>> queryUserImages(BaseUser user,
                                                   String category,
                                                   Integer pageNo,
                                                   Integer pageSize,
                                                   String fileName,
                                                   String startDate,
                                                   String endDate,
                                                   String businessId,
                                                   String modelType);

    /**
     * 删除用户全部图片
     * <p/>
     * 删除用户全部图片，返回操作应答
     *
     * @param baseUser 用户信息
     * @return 操作应答
     */
    Response<Boolean> deleteAll(BaseUser baseUser);


    /**
     * 根据OSS文件名删除用户全部图片
     *
     * @param fileServerName 服务器文件名
     * @return
     */
    Response<Boolean> deleteByFileName(List<String> fileServerName);

    /**
     * 更新业务ID
     *
     * @param fileId   文件ID
     * @param id       业务ID
     * @return
     */
    Response<Boolean> updateBusinessId(List<String> fileId, String id);

    /**
     * 将回调的参数（播放地址，封面地址），
     * 保存到file表里
     *
     * @param videoInfo
     * @return
     */
    Response<Boolean> updateVideoInfoByVideoId(Map<String, String> videoInfo);

    /**
     * 将回调的参数（播放地址）保存到file表里
     *
     * @param videoId 视频ID
     * @param playUrl 视频地址
     * @return
     */
    Response<Long> updateFileUrlByVodId(String videoId, String playUrl);

    /**
     * 将回调的参数（封面地址）保存到file表里
     *
     * @param videoId  视频ID
     * @param coverUrl 视频地址
     * @return
     */
    Response<Long> updateCoverUrlByVodId(String videoId, String coverUrl);
}
