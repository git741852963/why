package com.neusoft.features.common.utils;

import com.google.common.collect.Lists;
import org.dozer.DozerBeanMapper;

import java.util.Collection;
import java.util.List;

/**
 * Java Bean to Java Bean mapper Using Dozer。
 * <p/>
 * 参考：https://github.com/DozerMapper/dozer。
 *
 * @author andy.jiao@msn.com
 */
public class BeanMapper {
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }

    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }
}


