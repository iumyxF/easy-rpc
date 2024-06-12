package com.example.rpc.loadbalancer;

/**
 * @author iumyxF
 * @description: 负载均衡器常量
 * @date 2024/6/12 16:16
 */
public interface LoadBalancerKeys {

    /**
     * 源地址hash
     */
    String HASH = "HashLoadBalancer";

    /**
     * 轮询
     */
    String POLLING = "PollingLoadBalancer";

    /**
     * 随机
     */
    String RANDOM = "RandomLoadBalancer";

}
