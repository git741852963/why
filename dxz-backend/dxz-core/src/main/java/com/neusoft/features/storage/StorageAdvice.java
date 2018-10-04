package com.neusoft.features.storage;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class StorageAdvice {
    private static Logger logger = LoggerFactory.getLogger(StorageAdvice.class);
//    @Autowired
    private StoragePolicy storagePolicy;

    public Object storageGetAround(ProceedingJoinPoint jp)
            throws Throwable {
        Class clazz = ((MethodSignature) jp.getSignature()).getReturnType();


        StorageChain chain = this.storagePolicy.getStorageChain(clazz.getName());
        if ((chain == null) || (chain.getCurrent() == null)) {
            return jp.proceed();
        }
        String key = clazz.getSimpleName() + "@" + jp.getArgs()[0];
        Storable storable = chain.get(key);
        if ((storable == null) || (storable.isExpired())) {
            if ((storable != null) && (storable.isExpired())) {
                logger.debug("Object [{}] is expired!,call service:[{}]", key, jp.getTarget().getClass().getSimpleName());
            } else {
                logger.debug("Object [{}] not found in storage,call service:[{}]", key, jp.getTarget().getClass().getSimpleName());
            }
            Object obj = jp.proceed();
            logger.debug("Object[{}] Result form API:[{}]", key, obj);
            if (obj != null) {
                chain.putForward(new Storable(key, obj, Long.valueOf(chain.getExpiredSeconds())));
            }
            return obj;
        }
        return storable.target;
    }

    public void storageUpdateAround(ProceedingJoinPoint jp)
            throws Throwable {
        jp.proceed();
        if (jp.getArgs().length != 1) {
            return;
        }
        Object updateTarget = jp.getArgs()[0];
        Class clazz = updateTarget.getClass();


        StorageChain chain = this.storagePolicy.getStorageChain(clazz.getName());
        if (chain != null) {
            String key = clazz.getSimpleName() + "@" + BeanUtils.getProperty(updateTarget, "id");

            chain.remove(key);

            logger.debug("Object[{}] removed from storage.", key);
        }
    }
}