package com.example.rpc.serializer;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author iumyxF
 * @description: 序列化器枚举
 * @date 2024/6/1 16:11
 */
public enum SerializerEnums {

    /**
     * JDK序列化器
     */
    JDK(0, SerializerKeys.JDK),

    /**
     * JSON序列化器
     */
    JSON(1, SerializerKeys.JSON);

    private final int key;

    private final String value;

    SerializerEnums(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 key 获取枚举
     *
     * @param key
     * @return
     */
    public static SerializerEnums getEnumByKey(byte key) {
        for (SerializerEnums anEnum : SerializerEnums.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SerializerEnums getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SerializerEnums anEnum : SerializerEnums.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
