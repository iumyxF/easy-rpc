package com.example.rpc.spi;

import com.example.rpc.registry.Registry;
import com.example.rpc.serializer.Serializer;
import org.junit.Test;
import sun.misc.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/1 9:12
 */
public class SpiLoaderTest {

    @Test
    public void spiTest() throws IOException {
        Iterator<Serializer> serializerIterator = Service.providers(Serializer.class);
        while (serializerIterator.hasNext()) {
            Serializer serializer = serializerIterator.next();
            System.out.println(serializer.getClass().getName());
            byte[] bytes = serializer.serialize("hello");
            System.out.println(bytes.length);
        }
        ServiceLoader<Registry> registries = ServiceLoader.load(Registry.class);
        for (Registry registry : registries) {
            System.out.println(registry.getClass().getName());
        }
    }

    @Test
    public void loadTest() {
        SpiLoader.loadAll();
        Serializer instance = SpiLoader.getInstance(Serializer.class, "JsonSerializer");
        System.out.println(instance.getClass().getName());

        Serializer testSerializer = SpiLoader.getInstance(Serializer.class, "TestSerializer");
        System.out.println(testSerializer.getClass().getName());
    }
}