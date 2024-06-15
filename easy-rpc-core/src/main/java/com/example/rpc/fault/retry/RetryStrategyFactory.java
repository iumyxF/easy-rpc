package com.example.rpc.fault.retry;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: 重试策略工厂
 * @date 2024/6/15 9:39
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    public static RetryStrategy getExponentialBackoffRetryStrategy() {
        return getInstance(RetryStrategyKeys.EXPONENTIAL_BACKOFF);
    }

    public static RetryStrategy getFixedIntervalRetryStrategy() {
        return getInstance(RetryStrategyKeys.FIXED_INTERVAL);
    }

    public static RetryStrategy getNotRetryStrategy() {
        return getInstance(RetryStrategyKeys.NOT);
    }

    /**
     * 获取实例
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}
