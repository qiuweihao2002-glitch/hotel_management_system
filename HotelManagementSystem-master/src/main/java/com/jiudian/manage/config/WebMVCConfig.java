package com.jiudian.manage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /File/** 映射到容器里的 /app/upload/File/ 目录
        registry.addResourceHandler("/File/**")
                .addResourceLocations("file:/app/upload/File/");//我在容器上的时候把它挂载到了我本地的D盘
    }
}
