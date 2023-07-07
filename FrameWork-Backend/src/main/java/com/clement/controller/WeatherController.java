package com.clement.controller;


import com.clement.pojo.RestBean;
import com.clement.pojo.Weather;

import com.clement.service.impl.WeatherServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 14:13
 * @Desc
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    @Autowired
    WeatherServiceImpl weatherService;

    @GetMapping("/forecast")
    public RestBean<List<Weather>> weatherForecast() {
        try {
            List<Weather> forecastWeathers = weatherService.getForecastWeathers();
            return RestBean.succcess(forecastWeathers);

        } catch (JsonProcessingException e) {
            return RestBean.failure(500, null);
        }
    }

    @GetMapping("/current")
    public RestBean<Weather> weatherCurrent() {
        try {
            Weather weather = weatherService.getForecastWeathers().get(0);
            return RestBean.succcess(weather);

        } catch (JsonProcessingException e) {
            return RestBean.failure(500, null);
        }
    }


}
