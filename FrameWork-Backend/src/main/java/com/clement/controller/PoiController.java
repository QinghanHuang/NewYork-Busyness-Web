package com.clement.controller;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.RestBean;
import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/poi")
public class PoiController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;

    @GetMapping("/all")
    public RestBean<List<Poi>> getPoi(){
        try {
            List<Poi> poiLocations = poiService.getPoiLocation();
            return RestBean.succcess(poiLocations);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }

    @GetMapping("/{id}")
    public RestBean<PoiInfo> getPoiInfoById(@PathVariable("id") String id){
        try{
            PoiInfo poiInfo = poiService.getPoiInfoById(id);
            return RestBean.succcess(poiInfo);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }
}
