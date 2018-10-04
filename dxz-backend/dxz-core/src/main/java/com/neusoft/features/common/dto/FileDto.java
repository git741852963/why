package com.neusoft.features.common.dto;

import lombok.Data;

/**
 * 文件Model。
 *
 * @author andy.jiao@msn.com
 */
@Data
public class FileDto extends BaseData {

    /**
     * 所属用户ID
     */
    private Long userId;
    /**
     * 文件类型
     */
    private String fileCategory;
    /**
     * 文件原名
     */
    private String fileOriginalName;

    /**
     * 文件硬盘路径
     */
    private String filePath;
    /**
     * 文件扩展名
     */
    private String fileExtension;
    /**
     * 大小
     */
    private Long fileSize;

    /**
     * 缩略图
     */
    private String fileThumbPath;

    /**
     * 输出文件名（OSS）
     */
    private String fileServerName;

    /**
     * 模块
     */
    private String modelType;

    /**
     * 缩减图片源文件名称
     */
    private String subFileOriginalName;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 活动名称
     */
    private String activityNm;
}
