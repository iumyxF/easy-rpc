package com.example.rpc.server.netty.request;

import com.example.rpc.model.RpcRequest;
import org.junit.Test;

import java.util.*;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/31 10:01
 */
public class GetRequestHandlerTest {

    @Test
    public void parse() {
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("serviceName", Collections.singletonList("UserService"));
        parameters.put("methodName", Collections.singletonList("sayHello"));
        parameters.put("parameterTypes", Arrays.asList("java.lang.String", "java.lang.Integer"));
        parameters.put("args", Arrays.asList("zs", "lisi", "wangwu"));

        RpcRequest rpcRequest = new RpcRequest();
        parameters.forEach((key, value) -> {
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
                        try {
                            classes[i] = Class.forName(value.get(i));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
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
        });
        System.out.println(rpcRequest);
    }

}