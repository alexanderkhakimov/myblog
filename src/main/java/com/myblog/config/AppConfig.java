package com.myblog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.myblog.service", "com.myblog.dao"})
public class AppConfig {
}
