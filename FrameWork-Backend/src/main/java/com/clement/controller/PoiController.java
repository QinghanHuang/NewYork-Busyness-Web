package com.clement.controller;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
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

    @GetMapping
    public List<Poi> getPoi(){
        return poiService.getAllPoi();
    }
    @GetMapping("/{id}")
    public PoiInfo getPoiInfoById(@PathVariable("id") String id){
        return poiService.getPoiInfoById(id);
    }
}
