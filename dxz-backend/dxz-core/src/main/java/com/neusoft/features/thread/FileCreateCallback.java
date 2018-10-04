package com.neusoft.features.thread;

import com.neusoft.features.common.model.FileModel;

/**
 * 文件创建回调。
 *
 * @author andy.jiao@msn.com
 */
public interface FileCreateCallback {

    void done(boolean isSuccess, FileModel fileModel);
}
