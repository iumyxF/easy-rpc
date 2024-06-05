package com.example.provider.service;

import cn.hutool.core.date.DateUtil;
import com.example.easy.rpc.starter.annotation.RpcService;
import com.example.rpc.model.User;
import com.example.rpc.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/4 17:27
 */
@RpcService
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String sayHello(String name) {
        return "hello " + name + ", today is " + DateUtil.date();
    }

    @Override
    public User getUser(Long id) {
        return new User(9999L, "jack", 18);
    }
}
