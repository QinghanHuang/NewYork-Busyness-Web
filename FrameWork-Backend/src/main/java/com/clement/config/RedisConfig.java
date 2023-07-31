package com.clement.config;

import com.clement.pojo.prediction.PoiBusy;
import com.clement.pojo.prediction.ZoneBusy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 14:54
 * @Desc
 * @Version 1.0
 */
@Configuration

public class RedisConfig {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //use JsonRedisSerializer
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        // handle the serialization of LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules(); // Make sure the JSR310 module is registered
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean(name = "redisTemplateZoneBusys")
    public RedisTemplate<String, List<ZoneBusy>> redisTemplateZoneBusys(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<ZoneBusy>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //use JsonRedisSerializer
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        // handle the serialization of LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules(); // Make sure the JSR310 module is registered
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean(name = "redisTemplatePoiBusys")
    public RedisTemplate<String, List<PoiBusy>> redisTemplatePoiBusys(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<PoiBusy>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //use JsonRedisSerializer
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules(); // Make sure the JSR310 module is registered
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean(name = "redisTemplatePoiBusy")
    public RedisTemplate<String, PoiBusy> redisTemplatePoiBusy(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, PoiBusy> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //use JsonRedisSerializer
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules(); // Make sure the JSR310 module is registered
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
