package com.muftisaleh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.muftisaleh.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableJpaAuditing
@EnableCaching
public class MuftiSalehApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuftiSalehApplication.class, args);
    }
}
