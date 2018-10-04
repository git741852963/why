package com.neusoft.features.common.utils;

import com.neusoft.features.common.SystemEnviroment;
import com.neusoft.features.common.enums.FileType;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

/**
 * 文件操作辅助类。
 *
 * @author andy.jiao@msn.com
 */
@Component
public class FileUtil {

    // 目录符号
    private static final String HTTP_SEPARATOR = "http://";
    private static final String DIR_SEPARATOR = "/";

    // 散列划分大小
    private static final int DEFAULT_STEP = 2;
    private static final int DEFAULT_DEPTH = 2;

    private static SystemEnviroment env;

    @Autowired
    public void setEnv(SystemEnviroment env) {
        FileUtil.env = env;
    }

    /**
     * 生成文件名（UUID）
     *
     * @param fileType 文件扩展名
     * @return 文件名
     */
    public static String genFileName(String fileType) {
        if (!Strings.isNullOrEmpty(fileType) && !fileType.startsWith(".")) {
            fileType = ".".concat(fileType);
        }
        return UUID.randomUUID().toString().replace("-", "").concat(fileType);
    }

    /**
     * 生成文件路径。
     *
     * @param fileCategory 文件种类
     * @param fileName     文件名(uuid)
     * @return 文件Url
     */
    public static String genFileUrl(FileType fileCategory, String fileName) {
        String hashPath = genHashPath(fileName, DEFAULT_STEP, DEFAULT_DEPTH);

        if (Strings.isNullOrEmpty(hashPath)) {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        return buf.append(HTTP_SEPARATOR).append(env.getFileServerDomain()).append(DIR_SEPARATOR).append(
                fileCategory.value()).append(DIR_SEPARATOR).append(hashPath).append(DIR_SEPARATOR).append(fileName)
                  .toString();
    }

    /**
     * 生成文件路径。
     *
     * @param fileCategory 文件种类
     * @param fileName     文件名(uuid)
     * @return 文件路径
     */
    public static String genFilePath(FileType fileCategory, String fileName) {
        String path = genHashPath(fileName, DEFAULT_STEP, DEFAULT_DEPTH);

        if (Strings.isNullOrEmpty(path)) {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        return buf.append(env.getUploadFileRoot()).append(DIR_SEPARATOR).append(fileCategory.value()).append(
                DIR_SEPARATOR).append(path).toString();
    }

    /**
     * 分割uuid，形成文件路径
     *
     * @param uuid 用户uuid
     * @return 文件路径
     */
    private static String genHashPath(String uuid, int step, int depth) {
        if (Strings.isNullOrEmpty(uuid) || uuid.length() < (step * depth) || step < 1 || depth < 1) {
            return null;
        }

        String paths[] = new String[depth];
        for (int i = 0; i < depth; i++) {
            paths[i] = uuid.substring(step * i, (step * i) + step);
        }
        return StringUtils.join(paths, "/");
    }

    /**
     * 删除单个文件
     *
     * @param path 文件全路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);

        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param path 被删除目录的文件路径
     */
    public static void deleteDirectory(String path) {
        File dir = new File(path);

        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath());
            } // 删除子目录
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        // 删除当前目录
        dir.delete();
    }
}
