package com.example.consumer;

import com.example.consumer.service.ExampleServiceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/5 10:43
 */
@SpringBootTest(classes = ConsumerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ExampleServiceImplTest {

    @Autowired
    private ExampleServiceImpl exampleService;

    @Test
    public void test1() {
        exampleService.test();
    }

    @Ignore
    @Test
    public void testRetry(){
        exampleService.testRetry();
    }
}