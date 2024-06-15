package com.example.rpc.fault.retry;

import com.example.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author iumyxF
 * @description: 不进行重试
 * @date 2024/6/15 9:29
 */
public class NotRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
