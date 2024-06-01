package com.example.rpc.registry;


import com.example.rpc.config.RegistryConfig;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author iumyxF
 * @description: 本地注册中心 测试使用
 * @date 2024/5/14 14:22
 */
public class EtcdRegistry implements Registry{

    @Override
    public void init(RegistryConfig registryConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) {

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

    }

    @Override
    public void heartBeat() {

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        return null;
    }

    @Override
    public void watch(String serviceNodeKey) {

    }
}
