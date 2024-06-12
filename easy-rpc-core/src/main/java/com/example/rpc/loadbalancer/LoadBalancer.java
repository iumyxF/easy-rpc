package com.example.rpc.loadbalancer;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author iumyxF
 * @description: 负载均衡处理器
 * @date 2024/6/12 15:44
 */
public interface LoadBalancer {

    /**
     * 选举服务
     *
     * @param serviceMetaInfoList 服务列表
     * @param request             请求
     * @return 服务
     */
    ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, RpcRequest request);
}