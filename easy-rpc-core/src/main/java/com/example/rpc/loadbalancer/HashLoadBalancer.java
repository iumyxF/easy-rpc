package com.example.rpc.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.TreeMap;

/**
 * @author iumyxF
 * @description: 源地址哈希法
 * @date 2024/6/12 16:00
 */
public class HashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> serviceMap = new TreeMap<>();

    /**
     * 源地址哈希法，同一源地址的请求，当服务器列表不变时，它每次都会映射到同一台服务器进行访问。
     *
     * @param serviceMetaInfoList 服务列表
     * @param request             请求
     * @return 服务
     */
    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, RpcRequest request) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        for (int i = 0; i < size; i++) {
            serviceMap.put(i, serviceMetaInfoList.get(i));
        }
        // 计算ip地址哈希值
        int hash = request.getIpAddress() == null ? 0 : request.getIpAddress().hashCode() % serviceMetaInfoList.size();
        return serviceMap.get(hash);
    }

}
