package com.github.memberboardspring.config.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

@Configuration
public class WebBeanConfiguration {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
