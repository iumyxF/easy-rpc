package com.example.rpc.serializer;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: 序列化器工厂
 * @date 2024/6/1 10:29
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    public static Serializer getJdkSerializer() {
        return getInstance(SerializerKeys.JDK);
    }

    public static Serializer getJsonSerializer() {
        return getInstance(SerializerKeys.JSON);
    }

    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
