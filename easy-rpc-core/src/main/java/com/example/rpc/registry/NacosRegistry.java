package com.example.rpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.rpc.config.RegistryConfig;
import com.example.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author iumyxF
 * @description: Nacos 注册中心
 * @date 2024/5/14 13:54
 */
@Slf4j
public class NacosRegistry implements Registry {


    private NamingService namingService;

    /**
     * nacos中默认的分组名称
     */
    private static final String NACOS_NAMESPACE = "public";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String NACOS_ROOT_PATH = "/rpc/nacos";

    @Override
    public void init(RegistryConfig registryConfig) {
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, registryConfig.getAddress());
            properties.put(PropertyKeyConst.CONFIG_RETRY_TIME, registryConfig.getTimeout());
            properties.put(PropertyKeyConst.NAMESPACE, NACOS_NAMESPACE);
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        if (null != namingService) {
            try {
                // 该方法内包括了将所有服务下线处理
                namingService.shutDown();
                // 清空本地缓存
                if (!localRegisterNodeKeySet.isEmpty()) {
                    localRegisterNodeKeySet.clear();
                }
                registryServiceCache.clearCache();
                if (!watchingKeySet.isEmpty()) {
                    watchingKeySet.clear();
                }
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //注册到nacos
        namingService.registerInstance(serviceMetaInfo.getServiceKey(), serviceMetaInfo.getServiceGroup(), buildServiceInstance(serviceMetaInfo));
        // 添加节点信息到本地缓存
        String registerKey = NACOS_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            namingService.deregisterInstance(serviceMetaInfo.getServiceKey(), serviceMetaInfo.getServiceGroup(), buildServiceInstance(serviceMetaInfo));
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        // 从本地缓存移除
        String registerKey = NACOS_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public void heartBeat() {

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null && !cachedServiceMetaInfoList.isEmpty()) {
            return cachedServiceMetaInfoList;
        }
        try {
            // 从nacos中读取所有服务实例
            List<Instance> allInstances = namingService.getAllInstances(serviceKey);
            if (null == allInstances || allInstances.isEmpty()) {
                return null;
            }
            List<ServiceMetaInfo> metaInfoList = allInstances.stream()
                    .map(instance -> {
                        Map<String, String> metadata = instance.getMetadata();
                        ServiceMetaInfo metaInfo = new ServiceMetaInfo();
                        metaInfo.setServiceName(instance.getServiceName());
                        metaInfo.setServiceVersion(metadata.get("version"));
                        metaInfo.setServiceHost(instance.getIp());
                        metaInfo.setServicePort(instance.getPort());
                        metaInfo.setServiceGroup(metadata.get("group"));
                        return metaInfo;
                    })
                    .collect(Collectors.toList());
            if (!metaInfoList.isEmpty()) {
                // 写入本地缓存
                registryServiceCache.writeCache(metaInfoList);
            }
            return metaInfoList;
        } catch (NacosException e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void watch(String serviceNodeKey) {
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            try {
                namingService.subscribe(serviceNodeKey, event -> {
                    if (event instanceof NamingEvent) {
                        List<Instance> instanceList = ((NamingEvent) event).getInstances();
                        if (null != instanceList && !instanceList.isEmpty()) {
                            instanceList.forEach(instance -> {
                                //持久化实例健康检查失败后会被标记成不健康，而临时实例会直接从列表中被删除
                                if (!instance.isHealthy()) {
                                    registryServiceCache.clearCache();
                                }
                            });
                        }
                    }
                });
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 构建nacos 服务节点
     *
     * @param serviceMetaInfo 服务节点
     * @return nacos服务节点
     */
    private Instance buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        Instance instance = new Instance();
        instance.setIp(serviceMetaInfo.getServiceHost());
        instance.setPort(serviceMetaInfo.getServicePort());
        instance.setHealthy(true);
        instance.setServiceName(serviceMetaInfo.getServiceKey());
        // 设置为临时节点
        instance.setEphemeral(true);
        // 存放服务元信息
        Map<String, String> metadata = new HashMap<>(3);
        metadata.put("group", serviceMetaInfo.getServiceGroup());
        metadata.put("version", serviceMetaInfo.getServiceVersion());
        metadata.put("payload", JSONUtil.toJsonStr(serviceMetaInfo));
        instance.setMetadata(metadata);
        return instance;
    }
}
