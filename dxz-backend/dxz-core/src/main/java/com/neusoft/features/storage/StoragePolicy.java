package com.neusoft.features.storage;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoragePolicy {
    private static Logger logger = LoggerFactory.getLogger(StoragePolicy.class);
    private Map<String, StorageChain> storageChains = Maps.newConcurrentMap();

    public StoragePolicy(String[] chainNames, int[] expiredSeconds, Storage[]... storages) {
        for (int i = 0; i < chainNames.length; i++) {
            StorageChain storageChain = createChain(null, storages[i], 0);
            storageChain.setExpiredSeconds(expiredSeconds[i]);
            this.storageChains.put(chainNames[i], storageChain);
            logger.info("StorageChain [{}] init succeed,storages:[{}]", chainNames[i], Joiner.on(",").join(storages[i]));
        }
    }

    protected StorageChain getStorageChain(String chainName) {
        return (StorageChain) this.storageChains.get(chainName);
    }

    private StorageChain createChain(StorageChain pre, Storage[] storages, int idx) {
        StorageChain chain = new StorageChain();

        chain.setCurrent(storages[idx]);
        if (idx > 0) {
            chain.setPre(pre);
        }
        if (idx < storages.length - 1) {
            chain.setNext(createChain(chain, storages, idx + 1));
        }
        return chain;
    }
}
