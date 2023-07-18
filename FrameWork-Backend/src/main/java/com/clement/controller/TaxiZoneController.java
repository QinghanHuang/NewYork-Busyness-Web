package com.clement.controller;

import com.clement.pojo.RestBean;
import com.clement.pojo.TaxiZone;
import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaxiZoneController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;
    // get heatmap and 24 busy level numbers
    @GetMapping("/api/zone/all")
    public RestBean<List<TaxiZone>> getAllTaxiZones() {
        try{
            List<TaxiZone> taxiZones = poiService.getAllTaxiZones();
            return RestBean.succcess(taxiZones);
        } catch (JsonProcessingException e){
            return RestBean.failure(500, null);
        }
    }

    // post busy level to redis
    @PostMapping("/api/busy")
    public ResponseEntity<HttpStatus> postBusy(){
        poiService.updateBusy();
        return new ResponseEntity<>(HttpStatus.OK);
    }

//
//    @GetMapping("/api/get-busy")
//    public Map<String, List<Integer>> getBusyMap(){
//        return poiService.getBusyMap();
//    }

}

