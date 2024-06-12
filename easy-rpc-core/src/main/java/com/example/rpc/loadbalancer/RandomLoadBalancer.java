package com.example.rpc.loadbalancer;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Random;

/**
 * @author iumyxF
 * @description: 随机法
 * @date 2024/6/12 16:00
 */
public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, RpcRequest request) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null;
        }
        // 只有 1 个服务，不用随机
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
