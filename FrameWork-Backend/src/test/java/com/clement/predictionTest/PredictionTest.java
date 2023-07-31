package com.clement.predictionTest;

import com.clement.pojo.prediction.PoiBusy;
import com.clement.pojo.prediction.ZoneBusy;
import com.clement.service.DemoPmmlModelService;
import com.clement.service.PredictionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class PredictionTest {

    @Resource
    PredictionService predictionService;
    @Test
    public void getZoneBusysTest() {

        LocalDate today = LocalDate.now();
        List<ZoneBusy> zoneBusyList = predictionService.getZoneBusys(today);
        System.out.println(zoneBusyList);

    }

    @Test
    public void getPoiBusysTest() {
        LocalDate today = LocalDate.now();
        List<PoiBusy> poiBusyList = predictionService.getPoiBusys(today);
        System.out.println(poiBusyList);
    }

    @Test
    public void getPoiBusyById() {
        LocalDateTime now = LocalDateTime.now();
        PoiBusy poiBusy = predictionService.getPoiBusyById(1, now);
        System.out.println(poiBusy);
    }
}
