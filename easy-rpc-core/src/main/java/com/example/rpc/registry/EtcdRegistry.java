package com.example.rpc.registry;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.example.rpc.config.RegistryConfig;
import com.example.rpc.model.ServiceMetaInfo;
import com.example.rpc.serializer.JsonSerializer;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author iumyxF
 * @description: etcd 注册中心
 * etcd api文档 <a href="https://etcd.io/docs/v3.6/learning/api/"/>
 * jEtcd:使用文档<a href="https://github.com/etcd-io/jetcd"/>
 * @date 2024/5/14 14:22
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

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
    private static final String ETCD_ROOT_PATH = "/rpc/etcd/";

    @Override
    public void init(RegistryConfig registryConfig) {
        // create client using endpoints
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void destroy() {
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        if (null == serviceMetaInfo) {
            return;
        }
        // 创建租赁Client https://etcd.io/docs/v3.6/learning/api/#lease-api
        Lease leaseClient = client.getLeaseClient();
        // 60s的续租时间
        long leaseId = leaseClient.grant(60).get().getID();
        // 保存key-value
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JsonSerializer.OBJECT_MAPPER.writeValueAsString(serviceMetaInfo), StandardCharsets.UTF_8);
        kvClient.put(key, value, PutOption.builder().withLeaseId(leaseId).build()).get();
        //添加到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        // 也要从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public void heartBeat() {
        // 续签 20s 一次
        CronUtil.schedule("*/20 * * * * *", (Task) () -> localRegisterNodeKeySet.forEach(key -> {
            try {
                List<KeyValue> keyValueList = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                        .get()
                        .getKvs();
                if (CollUtil.isEmpty(keyValueList)) {
                    return;
                }
                // 只续签一个
                KeyValue keyValue = keyValueList.get(0);
                String valueString = keyValue.getValue().toString(StandardCharsets.UTF_8);
                ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(valueString, ServiceMetaInfo.class);
                register(serviceMetaInfo);
                log.info("server = {}, 续签成功", serviceMetaInfo.getServiceKey());
            } catch (Exception e) {
                throw new RuntimeException(key + "续签失败", e);
            }
        }));
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null && !cachedServiceMetaInfoList.isEmpty()) {
            return cachedServiceMetaInfoList;
        }
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;
        try {
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            // 前缀查询
                            GetOption.builder().isPrefix(true).build())
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // 写入服务缓存
            if (!serviceMetaInfoList.isEmpty()) {
                registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            }
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void watch(String serviceKey) {
        Watch watchClient = client.getWatchClient();
        boolean watchKey = watchingKeySet.add(serviceKey);
        if (watchKey) {
            String registerKey = ETCD_ROOT_PATH + serviceKey;
            ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
            watchClient.watch(key, response -> response.getEvents().forEach(e -> {
                if (WatchEvent.EventType.DELETE.equals(e.getEventType())) {
                    // 清理注册服务缓存
                    registryServiceCache.clearCache(serviceKey);
                }
            }));
        }
    }
}
