package com.example.rpc.registry;

import com.example.rpc.config.RegistryConfig;
import com.example.rpc.model.RpcConstant;
import com.example.rpc.model.ServiceMetaInfo;
import org.junit.*;

import java.util.List;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/14 14:46
 */
public class NacosRegistryTest {

    static final NacosRegistry nacosRegistry = new NacosRegistry();

    static ServiceMetaInfo serviceMetaInfo = null;

    @BeforeClass
    public static void setUp() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setRegistry(RegistryKeys.NACOS);
        registryConfig.setAddress("http://192.168.2.199:8848/nacos");
        registryConfig.setTimeout(10000L);
        nacosRegistry.init(registryConfig);

        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("UserService");
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost("192.168.2.199");
        serviceMetaInfo.setServicePort(9099);
    }

    @AfterClass
    public static void destroy() {
        //nacosRegistry.destroy();
    }

    @Test
    public void register() throws Exception {
        nacosRegistry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() {
    }

    @Test
    public void heartBeat() {
    }

    @Test
    public void serviceDiscovery() {
        List<ServiceMetaInfo> list = nacosRegistry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(list);
    }

    @Test
    public void watch() {
    }
}