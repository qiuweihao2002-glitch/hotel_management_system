package com.jiudian.manage.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession   // 启用 Spring Session + Redis
public class HttpSessionConfig {
}
//这个注解会把原来 Tomcat 内存里的 Session 替换成 “存到 Redis 里”