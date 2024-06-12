package com.example.rpc.loadbalancer;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: 负载均衡器工厂
 * @date 2024/6/12 16:19
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    public static LoadBalancer getHashInstance() {
        return getInstance(LoadBalancerKeys.HASH);
    }

    public static LoadBalancer getPollingInstance() {
        return getInstance(LoadBalancerKeys.POLLING);
    }

    public static LoadBalancer getRandomInstance() {
        return getInstance(LoadBalancerKeys.RANDOM);
    }

    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}