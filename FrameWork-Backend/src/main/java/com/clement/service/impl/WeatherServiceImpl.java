package com.clement.service.impl;

import com.clement.pojo.Weather;
import com.clement.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
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
    RedisTemplate<String, List<Weather>> redisTemplate;

    @Scheduled(fixedRate = 60 * 60 * 1000) // 1 hour (in milliseconds)
    public void fetchAndCacheWeatherData() {
        try {
            fetchWeatherDataFromApi();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Weather> getForecastWeathers() throws JsonProcessingException {
        //check if there is cache in redis
        List<Weather> redisWeathers = redisTemplate.opsForValue().get("forecast-weather");
        if (redisWeathers != null) {
            return redisWeathers;
        }
        return fetchWeatherDataFromApi();
    }

    private List<Weather> fetchWeatherDataFromApi() throws JsonProcessingException {
        String weatherResp = restTemplate.getForObject(weatherUrl, String.class);
        //read json by objectMapper
        JsonNode weatherJson = objectMapper
                .readTree(weatherResp)
                .get("list");

        List<Weather> weathers = new ArrayList<>();

        //new a local date
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        int i = 0;

        while (i < 40) {
            //get each element in list
            JsonNode weatherNode = weatherJson.get(i);

            //combine data we want
            String date = today.toString();
            String dayOfWeek = today.getDayOfWeek().toString();
            double temp = weatherNode.get("main").get("temp").asDouble();
            int humidity = weatherNode.get("main").get("humidity").asInt();
            double windSpeed = weatherNode.get("wind").get("speed").asDouble();
            int weatherId = weatherNode.get("weather").get(0).get("id").asInt();
            String weatherMain = weatherNode.get("weather").get(0).get("main").asText();
            String weatherDes = weatherNode.get("weather").get(0).get("description").asText();

            //creat a new Weather Object
            Weather weather = new Weather(date, dayOfWeek, temp, humidity, windSpeed, weatherId, weatherMain, weatherDes);
            //add it into list
            weathers.add(weather);

            //add i
            i = i == 0 ? i + 7 : i + 8;
            today = today.plusDays(1);
        }
        // store weathers in redis in json (take it out from the loop to avoid repetition)
        redisTemplate.opsForValue().set("forecast-weather", weathers, 60, TimeUnit.MINUTES);

        return weathers;
    }
}
