package com.example.consumer.service;

import com.example.easy.rpc.starter.annotation.RpcReference;
import com.example.rpc.model.User;
import com.example.rpc.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author iumyxF
 * @description: 测试调用
 * @date 2024/6/4 17:13
 */
@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;


    /**
     * 测试方法
     */
    public void test() {
        User user = userService.getUser(1001L);
        System.out.println("user = " + user);
        String hello = userService.sayHello(user.getName());
        System.out.println("hello = " + hello);
    }
}
