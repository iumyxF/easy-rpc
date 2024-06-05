package com.example.rpc.server.vertx.tcp;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/3 10:48
 */
public class VertxTcpServerTest {

    public static void main(String[] args) {
        new VertxTcpServer().start(18080);
    }

}