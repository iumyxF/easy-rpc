package com.example.rpc.server.netty.request;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzy
 * @description: 请求处理器工厂
 * @date 2024/5/31 9:16
 */
public class RequestHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(RequestHandlerFactory.class);

    public static final Map<HttpMethod, RequestHandler> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put(HttpMethod.GET, new GetRequestHandler());
        HANDLER_MAP.put(HttpMethod.POST, new PostRequestHandler());
    }

    public static RequestHandler getRequestHandler(HttpMethod httpMethod) {
        RequestHandler requestHandler = HANDLER_MAP.get(httpMethod);
        if (requestHandler == null) {
            log.error("requestHandler is null, httpMethod: {}", httpMethod);
            throw new RuntimeException("requestHandler is null");
        }
        return requestHandler;
    }
}
