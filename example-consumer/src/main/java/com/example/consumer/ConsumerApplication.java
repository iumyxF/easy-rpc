package com.example.consumer;

import com.example.easy.rpc.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/4 17:12
 */
@EnableRpc(enable = false)
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class);
    }
}
