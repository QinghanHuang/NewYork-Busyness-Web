package com.clement.controller;

import com.clement.pojo.RestBean;
import com.clement.pojo.prediction.PoiBusy;
import com.clement.service.impl.PredictionServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/21 13:12
 * @Desc
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/prediction")
public class PredictionController {

    @Resource(name= "predictionServiceImpl")
    private PredictionServiceImpl predictionService;
//    @GetMapping("/zone/{date}")
//    public RestBean<List<ZoneBusy>> predictZoneBusys(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
//        try {
//            List<ZoneBusy> zoneBusys = predictionService.getZoneBusys(date);
//            return RestBean.succcess(zoneBusys);
//        } catch (IllegalArgumentException e){
//            return RestBean.failure(400, null);
//        }
//    }
    @GetMapping("/poi/{date}")
    public RestBean<List<PoiBusy>> predictPoiBusys(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        try {
            List<PoiBusy> poiBusys = predictionService.getPoiBusys(date);
            return RestBean.succcess(poiBusys);
        } catch (IllegalArgumentException e){
            return RestBean.failure(400, null);
        }
    }

        @GetMapping("poi/{id}/{time}")
    public RestBean<PoiBusy> predictBusyById(@PathVariable int id, @PathVariable("time") @DateTimeFormat(pattern = "yyyy-MM-dd-HH") LocalDateTime time){
        try {
            PoiBusy poiBusy=predictionService.getPoiBusyById(id,time);
            return RestBean.succcess(poiBusy);
        } catch (IllegalArgumentException e){
            return RestBean.failure(400, null);
        }
    }

}
