package com.example.rpc.test;

import com.example.rpc.registry.LocalRegistry;
import com.example.rpc.server.NetServer;
import com.example.rpc.server.netty.NettyHttpServer;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/31 9:03
 */
public class NettyHttpServerTest {

    /**
     * 发送JSON数据进行测试 {"serviceName":"HelloService","methodName":"sayHello","serviceVersion":"1.0","parameterTypes":["java.lang.String"],"args":["zs"]}
     */
    public static void main(String[] args) {
        LocalRegistry.register("HelloService", HelloService.class);
        NetServer httpServer = new NettyHttpServer();
        httpServer.start(8080);
    }
}
