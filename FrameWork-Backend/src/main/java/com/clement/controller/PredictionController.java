package com.clement.controller;

import com.clement.pojo.RestBean;
import com.clement.service.PredictionService;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {
    @Resource
    private PredictionService predictionService;

    // For itinerary: get poi busy level by id and its busyness level
    @GetMapping("/{id}")
    public RestBean<Integer> predictBusyById(@PathVariable int id,
                                             @RequestParam("targetTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm") LocalDateTime targetTime){
        try {
            int hour = targetTime.getHour();
            int busy = predictionService.getBusyById(id, hour);
            // TODO: typo RestBean.success
            return RestBean.succcess(busy);
        } catch (IllegalArgumentException e){
            return RestBean.failure(400, null);
        }
    }

    // Get busyness levels for taxi zones on a specified date
    @GetMapping("/zone")
    public RestBean<List<HashMap<Integer, Integer>>> predictZoneBusys(@RequestParam("targetTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm") LocalDateTime targetTime){
        try {
            List<HashMap<Integer, Integer>> result = predictionService.getZoneBusys();
            return RestBean.succcess(result);
        } catch (IllegalArgumentException e){
            return RestBean.failure(400, null);
        }
    }

    // Get busyness levels for pois on a specified date
    @GetMapping("/poi")
    public RestBean<List<HashMap<Integer, Integer>>> predictPoiBusys(@RequestParam("targetTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm") LocalDateTime targetTime){
        try {
            List<HashMap<Integer, Integer>> result = predictionService.getPoiBusys();
            return RestBean.succcess(result);
        } catch (IllegalArgumentException e){
            return RestBean.failure(400, null);
        }
    }
}
