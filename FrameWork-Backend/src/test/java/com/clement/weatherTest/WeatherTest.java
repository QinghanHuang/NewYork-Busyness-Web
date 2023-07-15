package com.clement.weatherTest;

import com.clement.pojo.Weather;
import com.clement.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 14:34
 * @Desc
 * @Version 1.0
 */
@SpringBootTest
public class WeatherTest {

    @Autowired
    WeatherService weatherService;

    @Resource
    RedisTemplate<String,List<Weather>> redisTemplate;
    @Test
    public void test() throws JsonProcessingException {
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        String s=today.toString();
        System.out.println(s);
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        System.out.println(dayOfWeek);
    }
}
