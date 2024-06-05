package com.example.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author iumyxF
 * @description: 服务代理工厂 用于创建服务的代理对象
 * @date 2024/6/1 14:59
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}