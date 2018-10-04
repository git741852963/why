package com.neusoft.features.thread.carrier;

import com.neusoft.features.exception.FileCreateException;
import com.neusoft.features.thread.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件创建任务。
 *
 * @author andy.jiao@msn.com
 */
public class FileCreateTask implements AsyncTask {

    static final String DIR_SEPARATOR = "/";

    private InputStream is;
    private String path;
    private String fileName;

    public FileCreateTask(InputStream is, String path, String fileName) {
        super();
        this.is = is;
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    public void execute() {
        FileOutputStream fs = null;
        try {
            String[] folders = path.split(DIR_SEPARATOR);
            folders[1] = DIR_SEPARATOR + folders[1];

            String dir = "";
            boolean succ = true;
            for (int index = 1; succ && index < folders.length; index++) {
                if (index == 1) {
                    dir = folders[index];
                } else {
                    dir = dir + DIR_SEPARATOR + folders[index];
                }
                File file = new File(dir);
                if (!file.exists()) {
                    succ = file.mkdir();
                }
            }

            if (succ == false) {
                // Something wrong in generating output folder.
                System.err.println("Unable create folder for " + path);
                return;
            }
            File file = new File(path + DIR_SEPARATOR + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            fs = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 1024];
            int byteread = 0;
            while ((byteread = is.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
                fs.flush();
            }

        } catch (Exception e) {
            throw new FileCreateException("create file failed", e);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
