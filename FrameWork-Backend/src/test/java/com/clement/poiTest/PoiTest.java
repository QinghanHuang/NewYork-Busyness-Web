package com.clement.poiTest;

import com.clement.service.PoiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class PoiTest {
    @Autowired
    PoiService poiService;
    @Resource
    RedisTemplate<String, Map<Integer, List<Integer>>> redisTemplate;
    @Test
    public void test() throws JsonProcessingException {
        redisTemplate.opsForValue().get("busy");
    }
}
