package com.example.rpc.service;

import com.example.rpc.model.User;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/4 17:17
 */
public interface UserService {

    /**
     * 测试方法
     *
     * @param name
     * @return
     */
    String sayHello(String name);

    /**
     * 测试方法2
     *
     * @param id
     * @return
     */
    User getUser(Long id);
}
