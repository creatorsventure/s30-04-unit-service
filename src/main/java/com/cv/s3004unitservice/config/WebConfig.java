package com.cv.s3004unitservice.config;

import com.cv.s3004unitservice.service.component.RequestContextInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan({"com.cv"})
@EnableFeignClients(basePackages = "com.cv.s3004unitservice.service.feign")
@Configuration
@EnableCaching
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    private final RequestContextInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
