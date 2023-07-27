package com.clement.model_test;

import com.clement.pojo.prediction.PoiBusy;
import com.clement.service.DemoPmmlModelService;
import com.clement.service.PredictionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.In;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    PredictionService predictionService;

    @Test
    public void test() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int predict = pmmlModelService.predict(41.0, 11.0, 20.0, 5.0, i * 1.0, 0.000143093038);
            list.add(predict);
        }
        System.out.println(list);
    }

    @Test
    public void test1() {
        LocalDateTime time = LocalDateTime.now();
        PoiBusy poiBusyById = predictionService.getPoiBusyById(12, time);
        System.out.println(poiBusyById);

    }

    @Test
    public void test2() {
        LocalDate localDate = LocalDate.now();
        List<PoiBusy> poiBusys = predictionService.getPoiBusys(localDate);
        System.out.println(poiBusys);

    }

    @Test
    public void test3() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime time = LocalDateTime.of(2023,7,27,i,0);
            PoiBusy poiBusyById = predictionService.getPoiBusyById(1, time);

            list.add(poiBusyById.getBusy());
        }
        System.out.println(list);


    }
}
