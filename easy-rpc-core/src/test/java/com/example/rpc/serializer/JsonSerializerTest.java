package com.example.rpc.serializer;

import com.example.rpc.model.RpcRequest;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/14 11:04
 */
public class JsonSerializerTest {

    @Test
    public void serializeAndDeserialize() throws IOException {
        RpcRequest request = new RpcRequest();
        request.setServiceName("testServer");
        request.setMethodName("testSerialize");
        request.setServiceVersion("1.0");
        request.setParameterTypes(new Class[]{Integer.class, String.class, List.class, Map.class});
        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", "value2");
        request.setArgs(new Object[]{1, "testArg", Arrays.asList("aaa", "bbb", "ccc"), map});

        JsonSerializer serializer = new JsonSerializer();
        byte[] bytes = serializer.serialize(request);
        System.out.println(bytes.length);

        RpcRequest rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
        System.out.println(rpcRequest);
    }

}