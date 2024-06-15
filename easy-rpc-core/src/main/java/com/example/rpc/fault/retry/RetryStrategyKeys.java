package com.example.rpc.fault.retry;

/**
 * @author iumyxF
 * @description: 重试策略常量
 * @date 2024/6/15 9:41
 */
public interface RetryStrategyKeys {

    String NOT = "NotRetryStrategy";

    String FIXED_INTERVAL = "FixedIntervalRetryStrategy";

    String EXPONENTIAL_BACKOFF = "ExponentialBackoffRetryStrategy";
}
