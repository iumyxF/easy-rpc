package com.example.rpc.loadbalancer;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author iumyxF
 * @description: 轮询法
 * @date 2024/6/12 15:59
 */
public class PollingLoadBalancer implements LoadBalancer {

    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, RpcRequest request) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 只有一个服务，无需轮询
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 取模算法轮询
        int index = currentIndex.getAndUpdate(current -> (current == Integer.MAX_VALUE) ? 0 : current + 1) % size;
        return serviceMetaInfoList.get(index);
    }
}
