package com.example.rpc.fault.retry;

import com.example.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author iumyxF
 * @description: 重试策略
 * @date 2024/6/15 9:27
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
