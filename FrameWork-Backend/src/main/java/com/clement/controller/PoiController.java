package com.clement.controller;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.RestBean;
import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/poi")
public class PoiController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;

    // get all poi markers and 24 busy level numbers
    @GetMapping("/all")
    public RestBean<List<Poi>> getAllPois(){
        try {
            List<Poi> pois = poiService.getAllPois();
            return RestBean.succcess(pois);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }

    // get poi info by id
    @GetMapping("/{id}")
    public RestBean<PoiInfo> getPoiInfoById(@PathVariable("id") int id){
        try{
            PoiInfo poiInfo = poiService.getPoiInfoById(id);
            return RestBean.succcess(poiInfo);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }


}
