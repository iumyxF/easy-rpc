package com.example.rpc.server.vertx.tcp;

import com.example.rpc.model.RpcConstant;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.model.ServiceMetaInfo;

import java.util.concurrent.ExecutionException;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/3 10:48
 */
public class VertxTcpClientTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("HelloService");
        rpcRequest.setMethodName("sayHello");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"jack"});

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("HelloService");
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost("192.168.2.199");
        serviceMetaInfo.setServicePort(18080);
        serviceMetaInfo.setServiceGroup(RpcConstant.DEFAULT_GROUP);

        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo);
        System.out.println(rpcResponse);

    }

}