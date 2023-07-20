package com.clement.model_test;

import com.clement.service.DemoPmmlModelService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/20 23:56
 * @Desc
 * @Version 1.0
 */
@SpringBootTest
public class ModelTest {
    @Resource
    DemoPmmlModelService pmmlModelService;
    @Test
    public void test(){
        int predict = pmmlModelService.predict(100, 200);
        System.out.println(predict);
    }
}
