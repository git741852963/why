package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.common.model.FileModel;
import com.neusoft.features.dto.CommonDto;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ImageDao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class FileDao extends CommonDao {

    public int insert(FileModel base) {
        return getSqlSession().insert("system.file.insert", base);
    }

    public int update(FileModel base) {
        return getSqlSession().update("system.file.update", base);
    }

    public int delete(FileModel base) {
        return getSqlSession().update("system.file.deleteById", base);
    }

    public FileModel findById(FileModel base) {
        return getSqlSession().selectOne("system.file.findById", base);
    }

    public List<FileModel> findByCondition(FileModel fileModel) {
        return getSqlSession().selectList("system.file.findByCondition", fileModel);
    }

    public int delete(CommonDto<FileModel> base) {
        return getSqlSession().update("system.file.deleteByCondition", base);
    }

    public int deleteByFileName(CommonDto<FileModel> base) {
        return getSqlSession().update("system.file.deleteByFileName", base);
    }

    public List<FileModel> queryUserImages(Long userId,
                                           String category,
                                           int offset,
                                           int size,
                                           String fileName,
                                           String startDate,
                                           String endDate) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(7);
        params.put("userId", userId);
        params.put("category", category);
        params.put("offset", offset);
        params.put("size", size);
        params.put("fileName", fileName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return getSqlSession().selectList("system.file.paging", params);
    }

    public List<FileModel> queryUserImages(Long userId, String category, int offset, int size) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(4);
        params.put("userId", userId);
        params.put("category", category);
        params.put("offset", offset);
        params.put("size", size);

        return getSqlSession().selectList("system.file.paging", params);
    }

    public long getCountForQuery(Long userId, String category, String fileName, String startDate, String endDate) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(5);
        params.put("userId", userId);
        params.put("category", category);
        params.put("fileName", fileName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return getSqlSession().selectOne("system.file.count", params);
    }

    public long getCountForQuery(Long userId, String category) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("userId", userId);
        params.put("category", category);

        return getSqlSession().selectOne("system.file.count", params);
    }

    public long updateBusinessId(List<String> fileIds, String id) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        //params.put("userId", userId);
        params.put("fileIds", fileIds);
        params.put("id", id);

        return getSqlSession().selectOne("system.file.updateBusinessId", params);
    }

    public int updateVideoInfoByVideoId(Map<String, String> param) {
        return getSqlSession().insert("system.file.updateByVideoId", param);
    }

    public long updateFileUrlByVodId(String videoId, String playUrl) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("videoId", videoId);
        params.put("playUrl", playUrl);
        int count = getSqlSession().update("system.file.updateFileUrlByVodId", params);

        return (long) count;
    }

    public Long updateCoverUrlByVodId(String videoId, String coverUrl) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("videoId", videoId);
        params.put("coverUrl", coverUrl);
        int count = getSqlSession().update("system.file.updateCoverUrlByVodId", params);
        return (long) count;
    }

}
