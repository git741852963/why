package com.neusoft.features.common.utils;

import com.neusoft.features.common.annotation.Prop;
import com.neusoft.features.common.annotation.PropertiesFile;
import com.neusoft.features.exception.ProptiesInitializeException;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PropertiesHandler
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public final class PropertiesHelper {
    public static final String DIR_SEPARATOR = "/";

    private static ConcurrentMap<String, Object> propertiesMap = new ConcurrentHashMap();

    private static ConcurrentMap<String, Long> lastModifyTimeMap = new ConcurrentHashMap();

    @SuppressWarnings("unchecked")
    public static final synchronized <T> T getProperties(Class<T> clazz) {
        T instance;

        try {
            Constructor<? extends Object> constructor = clazz.getConstructor();
            instance = (T) constructor.newInstance();
        } catch (Exception e1) {
            throw new ProptiesInitializeException(e1.getMessage());
        }

        // check annotation
        if (!clazz.isAnnotationPresent(PropertiesFile.class)) {
            return instance;
        }

        // read properties file.
        PropertiesFile annotationProperties = clazz.getAnnotation(PropertiesFile.class);
        String fileName = annotationProperties.value();
        String dir = annotationProperties.dir();

        Object properties = propertiesMap.get(fileName);
        Long lastModifyTime = lastModifyTimeMap.get(fileName);

        if (properties == null) {
            // first load
            File propertiesFile = getPropertiesFile(fileName, dir);
            Long fileModifiedTime = propertiesFile.lastModified();
            instance = getNullableProperties(dir, fileName, clazz, instance, fileModifiedTime);
        } else {
            if (lastModifyTime + 60000 < new Date().getTime()) {
                // cache expired
                File propertiesFile = getPropertiesFile(fileName, dir);
                Long fileModifiedTime = propertiesFile.lastModified();
                if (fileModifiedTime > lastModifyTime) {
                    instance = getNullableProperties(dir, fileName, clazz, instance, fileModifiedTime);
                }
            } else {
                // return cached properties class
                return (T)properties;
            }
        }

        return instance;
    }

    private static <T> T getNullableProperties(String dir, String fileName, Class<T> clazz, T instance, long fileModifiedTime) {
        NullableProperties properties = (NullableProperties) loadProperties(dir, fileName);
        toPojo(properties, clazz, instance);

        propertiesMap.put(fileName, instance);
        lastModifyTimeMap.put(fileName, fileModifiedTime);

        return instance;
    }

    private static <T> void toPojo(Properties properties, Class<T> clazz, T instance) {
        Method[] methods = clazz.getMethods();

        if (instance instanceof Properties) {
            Properties props = (Properties) instance;
            props.putAll(properties);
        } else {
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (!Modifier.isPublic(mod) || !method.isAnnotationPresent(Prop.class)) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    continue;
                }

                Prop annotationProperty = method.getAnnotation(Prop.class);
                String key = annotationProperty.key();
                String defaultValue = annotationProperty.defaultValue();
                String propertyValue = properties.getProperty(key, defaultValue);
                try {
                    method.invoke(instance, ConvertUtils.convert(propertyValue, parameterTypes[0]));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Properties loadProperties(String dir, String fileName) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            String path = fileName;
            if (!Strings.isNullOrEmpty(dir)) {
                path = dir + DIR_SEPARATOR + fileName;
            }

            input = Resources.asByteSource(Resources.getResource(path)).openStream();
            properties.load(input);
        } catch (Exception e) {
            log.error("failed to load direct_render.properties", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return properties;
    }

    private static File getPropertiesFile(String filename, String defaultDir) {

        defaultDir = processPath(defaultDir);

        File file = new File(defaultDir, filename);
        if (!file.exists()) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null) {
                    loader = PropertiesHelper.class.getClassLoader();
                }
                if (loader != null) {
                    URL url = file.isAbsolute() ? loader.getResource(filename) : loader.getResource(file.getPath());
                    if (url != null) {
                        String newFilename = URLDecoder.decode(url.getFile(), "ISO-8859-1");
                        file = new File(newFilename);
                    }
                }
                if (!file.exists()) {
                    // when file in the jar,an absolute file to be create;
                    file = new File(DIR_SEPARATOR + defaultDir + DIR_SEPARATOR + filename);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private static String processPath(String defaultDir) {
        Pattern pattern = Pattern.compile("\\$\\{(\\p{Alpha}+[\\w\\.]*)\\}");
        Matcher matcher = pattern.matcher(defaultDir);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String value = System.getProperty(variable);
            if (value != null) {
                value = value.replaceAll("\\\\", "\\\\\\\\");
                matcher.appendReplacement(sb, value);
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static class NullableProperties extends Properties {

        private static final long serialVersionUID = 1L;

        @Override
        public String getProperty(String key, String defaultValue) {
            String origin = super.getProperty(key, defaultValue);
            if (origin != null && origin.trim().length() == 0) {
                // 空白
                return defaultValue;
            }
            return origin;
        }
    }
}
