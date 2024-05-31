package com.example.rpc.server.netty.request;

import com.example.rpc.model.RpcRequest;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author iumyxF
 * @description: 请求处理器
 * @date 2024/5/31 9:17
 */
public interface RequestHandler {

    /**
     * 处理http请求
     *
     * @param httpRequest http请求信息
     * @return rpc请求信息
     */
    RpcRequest handle(FullHttpRequest httpRequest);
}
