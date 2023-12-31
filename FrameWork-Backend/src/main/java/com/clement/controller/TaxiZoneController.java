package com.clement.controller;

import com.clement.pojo.RestBean;
import com.clement.pojo.TaxiZone;
import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zone/")
public class TaxiZoneController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;

    // get all taxi zones on heatmap
    @GetMapping("/all")
    public RestBean<List<TaxiZone>> getAllTaxiZones() {
        try{
            List<TaxiZone> taxiZones = poiService.getAllTaxiZones();
            return RestBean.succcess(taxiZones);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }
}

