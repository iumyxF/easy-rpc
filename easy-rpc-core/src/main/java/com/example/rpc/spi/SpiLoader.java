package com.example.rpc.spi;

import com.example.rpc.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iumyxF
 * @description: SPI（Service Provider Interface） 加载器
 * <a href="https://www.jianshu.com/p/3a3edbcd8f24%20%20#%E4%BD%BF%E7%94%A8"/>
 * <p>
 * 作用：按需加载服务实现类
 * @date 2024/5/31 16:26
 */
public class SpiLoader {

    private static final Logger log = LoggerFactory.getLogger(SpiLoader.class);

    /**
     * 存储已加载的类：< 接口名, <实现类类名,实现类> >
     */
    private static final Map<String, Map<String, Class<?>>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 懒加载
     * 对象实例缓存 < 类路径 ,对象实例 >
     */
    private static final Map<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 加载列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Collections.singletonList(Serializer.class);

    public static void loadAll() {
        log.info("load all SPI");
        LOAD_CLASS_LIST.forEach(SpiLoader::load);
    }

    /**
     * 加载该类的实现类
     *
     * @param clazz 目标类
     * @param <T>   泛型
     */
    public static <T> void load(Class<T> clazz) {
        HashMap<String, Class<?>> clazzMap = new HashMap<>(16);
        Iterator<T> providers = Service.providers(clazz);
        while (providers.hasNext()) {
            T t = providers.next();
            clazzMap.put(t.getClass().getSimpleName(), t.getClass());
        }
        if (!clazzMap.isEmpty()) {
            LOADER_MAP.put(clazz.getName(), clazzMap);
        }
    }

    public static <T> T getInstance(Class<T> tClass, String targetClassName) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> clazzMap = LOADER_MAP.get(tClassName);
        if (clazzMap == null) {
            throw new RuntimeException(String.format("SpiLoader is not loaded with the %s type", tClassName));
        }
        if (!clazzMap.containsKey(targetClassName)) {
            throw new RuntimeException(String.format("SpiLoader %s interface does not exist as a type of impl = %s type", tClassName, targetClassName));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = clazzMap.get(targetClassName);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!INSTANCE_CACHE.containsKey(implClassName)) {
            try {
                INSTANCE_CACHE.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s instance fail", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) INSTANCE_CACHE.get(implClassName);
    }
}
