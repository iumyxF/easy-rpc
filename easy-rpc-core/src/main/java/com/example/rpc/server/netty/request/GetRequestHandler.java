package com.example.rpc.server.netty.request;

import com.example.rpc.model.RpcRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/31 9:19
 */
public class GetRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(GetRequestHandler.class);

    @Override
    public RpcRequest handle(FullHttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
        Map<String, List<String>> parameters = decoder.parameters();
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest();
        // 解析字段
        try {
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                switch (key) {
                    case "serviceName":
                        rpcRequest.setServiceName(value.get(0));
                        break;
                    case "methodName":
                        rpcRequest.setMethodName(value.get(0));
                        break;
                    case "serviceVersion":
                        rpcRequest.setServiceVersion(value.get(0));
                        break;
                    case "parameterTypes":
                        Class<?>[] classes = new Class<?>[value.size()];
                        for (int i = 0; i < value.size(); i++) {
                            classes[i] = Class.forName(value.get(i));
                        }
                        rpcRequest.setParameterTypes(classes);
                        break;
                    case "args":
                        Object[] args = new Object[value.size()];
                        for (int i = 0; i < value.size(); i++) {
                            args[i] = value.get(i);
                        }
                        rpcRequest.setArgs(args);
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("类型解析异常,{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return rpcRequest;
    }
}