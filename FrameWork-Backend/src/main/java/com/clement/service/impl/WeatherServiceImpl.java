package com.clement.service.impl;

import com.clement.pojo.Weather;
import com.clement.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 13:54
 * @Desc
 * @Version 1.0
 */
@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${weather.url}")
    String weatherUrl;

    //to call api
    RestTemplate restTemplate = new RestTemplate();

    //to handle json
    ObjectMapper objectMapper = new ObjectMapper();

    //to store in redis
    @Resource
    RedisTemplate<String,List<Weather>> redisTemplate;

    @Override
    public List<Weather> getForecastWeathers() throws JsonProcessingException {
        //check if there is cache in redis
        List<Weather> redisWeathers= redisTemplate.opsForValue().get("forecast-weather");
        if(redisWeathers!=null){
            return redisWeathers;
        }

        //if no call api to fetch weather
        String weatherResp = restTemplate.getForObject(weatherUrl, String.class);
        //read json by objectMapper
        JsonNode weatherJson = objectMapper
                .readTree(weatherResp)
                .get("list");

        List<Weather> weathers = new ArrayList<>();

        //
        for (int i = 0; i < 40; i++) {
            //get each element in list
            JsonNode weatherNode = weatherJson.get(i);

            //combine data we want
            long timeStamp = weatherNode.get("dt").asLong();
            String timeString= weatherNode.get("dt_txt").asText();
            double temp = weatherNode.get("main").get("temp").asDouble();
            int humidity = weatherNode.get("main").get("humidity").asInt();
            double windSpeed = weatherNode.get("wind").get("speed").asDouble();
            int weatherId = weatherNode.get("weather").get(0).get("id").asInt();
            String weatherMain = weatherNode.get("weather").get(0).get("main").asText();
            String weatherDes = weatherNode.get("weather").get(0).get("description").asText();

            //creat a new Weather Object
            Weather weather = new Weather(timeStamp,timeString, temp, humidity, windSpeed, weatherId, weatherMain, weatherDes);
            //add it into list
            weathers.add(weather);



//            store weathers in redis in json
            redisTemplate.opsForValue().set("forecast-weather",weathers,60, TimeUnit.MINUTES);
        }


        return weathers;
    }
}
