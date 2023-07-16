package com.clement.controller;

import com.clement.pojo.RestBean;
import com.clement.pojo.TaxiZone;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zone")
public class TaxiZoneController {
    @Resource(name = "poiServiceImpl")
    private PoiService poiService;
    @GetMapping
    public RestBean<List<TaxiZone>> getAllTaxiZone() {
        return poiService.getTaxiZoneLocation();
    }

}

