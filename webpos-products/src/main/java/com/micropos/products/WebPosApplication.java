package com.micropos.products;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@Configuration
//@EnableAutoConfiguration()
@EnableJpaRepositories(basePackages = "com.micropos.products.jpa")
@SpringBootApplication
//ComponentScan(basePackages = "com.micropos.products")
@EntityScan(basePackages = "com.micropos.products.model")
@EnableCaching
@EnableDiscoveryClient
public class WebPosApplication {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    public static void main(String[] args) {
        SpringApplication.run(WebPosApplication.class, args);
    }

}
