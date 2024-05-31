package com.example.rpc.server;

/**
 * HTTP 服务器
 *
 * @author iumyxF
 * @date 2024/5/13 20:55
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port 端口
     */
    void start(int port);

    /**
     * 停止服务器
     */
    void stop();
}
