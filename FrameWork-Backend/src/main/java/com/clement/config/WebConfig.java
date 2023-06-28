package com.clement.config;

import com.clement.intercerpor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @Author Qinghan Huang
 * @Date 2023/6/27 23:04
 * @Desc
 * @Version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/**","/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**");
//

    }
//    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

}
