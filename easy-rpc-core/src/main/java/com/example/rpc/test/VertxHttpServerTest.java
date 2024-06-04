package com.example.rpc.test;

import com.example.rpc.registry.LocalRegistry;
import com.example.rpc.server.NetServer;
import com.example.rpc.server.vertx.http.VertxHttpServer;

/**
 * @author iumyxF
 * @description: 测试代码
 * @date 2024/5/30 11:16
 */
public class VertxHttpServerTest {

    /**
     * 发送JSON数据进行测试 {"serviceName":"HelloService","methodName":"sayHello","serviceVersion":"1.0","parameterTypes":["java.lang.String"],"args":["zs"]}
     */
    public static void main(String[] args) {
        NetServer httpServer = new VertxHttpServer();
        httpServer.start(8080);
        LocalRegistry.register("HelloService", HelloService.class);
    }
}
