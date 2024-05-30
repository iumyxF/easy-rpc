package com.example.rpc.registry;

import com.example.rpc.config.RegistryConfig;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author iumyxF
 * @description: 注册中心
 * @date 2024/5/14 11:37
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig 注册中心配置信息
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 注册服务（服务端）
     *
     * @param serviceMetaInfo 服务信息
     * @throws Exception 异常
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务（服务端）
     *
     * @param serviceMetaInfo 服务信息
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 心跳检测（服务端）
     */
    void heartBeat();

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     *
     * @param serviceKey 服务键名
     * @return 服务列表
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey 服务信息nodeKey
     */
    void watch(String serviceNodeKey);
}
