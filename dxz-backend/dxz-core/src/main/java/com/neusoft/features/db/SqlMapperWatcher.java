package com.neusoft.features.db;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Sql Mapper Watcher
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public class SqlMapperWatcher implements Runnable, InitializingBean {

    // 延迟刷新秒数
    private static int delaySeconds = 20;
    // 休眠时间
    private static int sleepSeconds = 5;
    // Mapper实际资源路径
    private Set<String> location;
    // Mapper资源路径
    private Resource[] mapperLocations;
    // MyBatis配置对象
    private Configuration configuration;
    // 上一次刷新时间
    private Long beforeTime = 0L;
    // sql session factory bean
    private SqlSessionFactory sqlSessionFactory;
    // configuration map列表
    private String[] mapFieldNames = new String[]{"mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments"};

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) throws Exception {
        this.sqlSessionFactory = sqlSessionFactory;
        this.configuration = this.sqlSessionFactory.getConfiguration();
    }

    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public void setDelaySeconds(int seconds) {
        delaySeconds = seconds >= 20 ? seconds : 20;
    }

    public void setSleepSeconds(int seconds) {
        sleepSeconds = seconds >= 5 ? seconds : 5;
    }

    /**
     * 执行刷新
     *
     * @param path       刷新目录
     * @param beforeTime 上次刷新时间
     * @throws Exception 异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void refresh(String path, Long beforeTime) throws Exception {

        // 本次刷新时间
        Long refreshTime = System.currentTimeMillis();

        // 获取需要刷新的Mapper文件列表
        List<File> fileList = this.getRefreshFile(new File(path), beforeTime);
        if (fileList.size() == 0) {
            return;
        }

        // 循环刷新
        for (int i = 0; i < fileList.size(); i++) {
            InputStream inputStream = new FileInputStream(fileList.get(i));
            String filePath = fileList.get(i).getAbsolutePath();
            log.debug("【SQL Mapper Watcher】refresh sql mapper:{}, path={}", fileList.get(i).getName(), filePath);

            try {
                // 清理已加载的资源标识，方便让它重新加载。
                Field loadedResourcesField = configuration.getClass().getDeclaredField("loadedResources");
                loadedResourcesField.setAccessible(true);
                Set loadedResourcesSet = ((Set) loadedResourcesField.get(configuration));
                loadedResourcesSet.remove(filePath);

                //重新编译加载资源文件。
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration, filePath,
                                                                         configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (Exception e) {
                throw new NestedIOException("Failed to parse mapping resource: '" + filePath + "'", e);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        // 如果刷新了文件，则修改刷新时间，否则不修改
        if (fileList.size() > 0) {
            this.beforeTime = refreshTime;
        }
    }

    /**
     * 获取需要刷新的文件列表
     *
     * @param file       文件/目录
     * @param beforeTime 上次刷新时间
     * @return 刷新文件列表
     */
    private List<File> getRefreshFile(File file, Long beforeTime) {
        List<File> files = new ArrayList<>();

        if (file == null) {
            return files;
        }

        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (int i = 0; i < subFiles.length; i++) {
                    files.addAll(this.getRefreshFile(subFiles[i], beforeTime));
                }
            }
        } else {
            if (file.lastModified() > beforeTime) {
                files.add(file);
            }
        }

        return files;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    /**
     * 重写 org.apache.ibatis.session.Configuration.StrictMap 类
     * 来自 MyBatis3.4.0版本，修改 put 方法，允许反复 put更新。
     */
    public static class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = 1L;
        private String name;

        public StrictMap(String name, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            this.name = name;
        }

        public StrictMap(String name, int initialCapacity) {
            super(initialCapacity);
            this.name = name;
        }

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        public StrictMap(String name, Map<String, ? extends V> m) {
            super(m);
            this.name = name;
        }

        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (value instanceof Ambiguity) {
                throw new IllegalArgumentException(
                        ((Ambiguity) value).getSubject() + " is ambiguous in " + name + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        @SuppressWarnings("unchecked")
        public V put(String key, V value) {
            // 如果现在状态为刷新，则刷新(先删除后添加)
            remove(key);
            SqlMapperWatcher.log.debug("【SQL Mapper Watcher】refresh:" + key.substring(key.lastIndexOf(".") + 1));

            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key);
            }
            if (key.contains(".")) {
                final String shortKey = getShortName(key);
                if (super.get(shortKey) == null) {
                    super.put(shortKey, value);
                } else {
                    super.put(shortKey, (V) new Ambiguity(shortKey));
                }
            }
            return super.put(key, value);
        }

        private String getShortName(String key) {
            final String[] keyparts = key.split("\\.");
            return keyparts[keyparts.length - 1];
        }

        protected static class Ambiguity {
            private String subject;

            public Ambiguity(String subject) {
                this.subject = subject;
            }

            public String getSubject() {
                return subject;
            }
        }
    }

    @Override
    public void run() {

        // 整理locations
        if (location == null) {
            location = Sets.newHashSet();
            log.debug("【SQL Mapper Watcher】sql mapper's length:" + mapperLocations.length);
            for (Resource mapperLocation : mapperLocations) {
                String s = "";
                try {
                    s = mapperLocation.getFile().getAbsolutePath().replaceAll("\\\\", "/");
                } catch (IOException e) {
                    e.printStackTrace();
                    log.warn("【SQL Mapper Watcher】error occured, exception={}", e.getMessage());
                }
                if (!location.contains(s)) {
                    location.add(s);
                    log.debug("【SQL Mapper Watcher】add ocation:" + s);
                }
            }
            log.debug("【SQL Mapper Watcher】sql mapper file count:" + location.size());
        }

        log.debug("【SQL Mapper Watcher】watching sql mappers={}", location);

        try {
            // 清理原有资源，更新为自己的StrictMap，方便增量重新加载
            for (String fieldName : mapFieldNames) {
                Field field = configuration.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Map map = ((Map) field.get(configuration));
                if (!(map instanceof StrictMap)) {
                    Map newMap = new StrictMap(StringUtils.capitalize(fieldName) + "collection");
                    for (Object key : map.keySet()) {
                        try {
                            newMap.put(key, map.get(key));
                        } catch (IllegalArgumentException ex) {
                            log.warn("【SQL Mapper Watcher】replace StrictMap failed, cause:{}", ex.getMessage());
                            newMap.put(key, ex.getMessage());
                        }
                    }
                    field.set(configuration, newMap);
                }
            }
        } catch (Exception ex) {
            log.warn("【SQL Mapper Watcher】error occured when updating configuration: {}", ex.getMessage());
        }

        // 启动刷新线程
        final SqlMapperWatcher watcher = this;
        beforeTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    Thread.sleep(delaySeconds * 1000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    log.warn("【SQL Mapper Watcher】error occured, exception={}", e2.getMessage());
                }

                while (true) {
                    log.info("【SQL Mapper Watcher】========= checking mybatis mapper =========, t=" + new Date().getTime());

                    try {
                        for (String filePath : location) {
                            watcher.refresh(filePath, beforeTime);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        log.warn("【SQL Mapper Watcher】error occured, exception={}", e1.getMessage());
                    }
                    try {
                        Thread.sleep(sleepSeconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log.warn("【SQL Mapper Watcher】error occured, exception={}", e.getMessage());
                    }
                }
            }
        }, "sql-mapper-watcher").start();
    }
}