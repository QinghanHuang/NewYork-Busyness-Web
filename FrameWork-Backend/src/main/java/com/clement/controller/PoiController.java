package com.clement.controller;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.RestBean;
import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class PoiController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;

    // get all poi markers and 24 busy level numbers
    @GetMapping("/api/poi/all")
    public RestBean<List<Poi>> getAllPois(){
        try {
            List<Poi> pois = poiService.getAllPois();
            return RestBean.succcess(pois);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }

    // get poi info by poi id and its current busy level
    @GetMapping("api/poi-info/{id}")
    public RestBean<PoiInfo> getPoiInfoById(@PathVariable("id") int id){
        try{
            PoiInfo poiInfo = poiService.getPoiInfoById(id);
            return RestBean.succcess(poiInfo);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }

    // get poi busy level by poi id and its busy level at a future time point
    @GetMapping("api/prediction/{id}")
    public RestBean<Integer> getPoiBusy(@PathVariable int id,
                                        @RequestParam("targetTime") @DateTimeFormat(pattern = "yyyy-MM-dd-HH") LocalDateTime targetTime) {
        String zid = String.valueOf(poiService.getZidById(id));
        LocalDateTime currentTime = LocalDateTime.now();
        int hoursAhead = (int) ChronoUnit.HOURS.between(currentTime, targetTime);

        if(hoursAhead >= 0 && hoursAhead < 24){
            int busy = poiService.getBusyMap().get(zid).get(hoursAhead);
            return RestBean.succcess(busy);
        } else {
            return RestBean.failure(500,null);
        }
    }
}
