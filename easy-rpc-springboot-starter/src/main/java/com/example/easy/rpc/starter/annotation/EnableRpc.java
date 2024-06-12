package com.example.easy.rpc.starter.annotation;


import com.example.easy.rpc.starter.bootstrap.RpcConsumerBootstrap;
import com.example.easy.rpc.starter.bootstrap.RpcInitBootstrap;
import com.example.easy.rpc.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author iumyxF
 * @description: Rpc 启动注解 用于控制rpc服务器的启动
 * @date 2024/6/1 14:38
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     *
     * @return
     */
    boolean enable() default true;
}
