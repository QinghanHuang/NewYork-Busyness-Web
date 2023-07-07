package com.clement.service;

import com.clement.pojo.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface WeatherService {
    List<Weather> getForecastWeathers() throws JsonProcessingException;
}
