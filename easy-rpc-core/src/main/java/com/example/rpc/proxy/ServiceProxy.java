package com.example.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import com.example.rpc.RpcApplication;
import com.example.rpc.config.RpcConfig;
import com.example.rpc.fault.retry.RetryStrategy;
import com.example.rpc.fault.retry.RetryStrategyFactory;
import com.example.rpc.loadbalancer.LoadBalancer;
import com.example.rpc.loadbalancer.LoadBalancerFactory;
import com.example.rpc.model.RpcConstant;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.model.ServiceMetaInfo;
import com.example.rpc.registry.Registry;
import com.example.rpc.registry.RegistryFactory;
import com.example.rpc.server.vertx.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/1 15:17
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .ipAddress(NetUtil.getLocalhostStr())
                .build();
        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        // 从注册中心中获取服务实例
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("server address not found");
        }
        // 通过负载均衡算法获取服务
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        ServiceMetaInfo select = loadBalancer.select(serviceMetaInfoList, rpcRequest);
        // 获取重试策略执行请求
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
        RpcResponse rpcResponse = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, select));
        log.info("远程调用结果 rpcResponse = {}", rpcResponse);
        return rpcResponse.getData();
    }
}
