package com.neusoft.features.api.condition;

import com.neusoft.features.api.constants.ApiHeaderConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * api版本条件筛选器
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public class ApiVerCondition implements RequestCondition<ApiVerCondition> {

    @Getter
    private double minVersion;

    @Getter
    private double maxVersion;

    public ApiVerCondition(double min, double max) {
        this.minVersion = min;
        this.maxVersion = max;
        log.debug("[API VER] new ApiVerCondition:min:{} max:{}", min, max);
    }

    /**
     * 将不同的筛选条件合并
     */
    public ApiVerCondition combine(ApiVerCondition other) {
        // 采用最后定义优先原则，则方法上的定义覆盖类上面的定义
        log.debug("[API VER] combine: this.min:{} this.max:{}, other.min:{} other.max:{}", minVersion, maxVersion, other.minVersion, other.maxVersion);
        return new ApiVerCondition(other.getMinVersion(), other.getMaxVersion());
    }

    /**
     * 根据request查找匹配到的筛选条件
     */
    public ApiVerCondition getMatchingCondition(HttpServletRequest request) {
        String targetVersion = request.getHeader(ApiHeaderConstants.X_API_VER);
        if (targetVersion != null && !"".equals(targetVersion)) {
            Double version = Double.valueOf(targetVersion);
            // 目标版本号必须在最小/最大版本号区间内
            if (version >= minVersion && version <= maxVersion) {
                log.debug("[API VER] get matching condition: this.min:{} this.max:{}, request.x-api-version:{}", minVersion, maxVersion, targetVersion);
                return this;
            }
        }
        return null;
    }

    /**
     * 不同筛选条件比较,用于排序
     */
    public int compareTo(ApiVerCondition other, HttpServletRequest request) {
        // 比较最小版本号大小，优先匹配最新的版本号
        int result = 0;
        if (other.getMinVersion() > this.minVersion) {
            result = 1;
        } else if (other.getMinVersion() < this.minVersion) {
            result = -1;
        }

        log.debug("[API VER] compareTo: this.min:{} this.max:{}, other.min:{} other.max:{}, result:{}", minVersion, maxVersion, other.getMinVersion(), other.getMaxVersion(), result);
        return result;
    }
}


