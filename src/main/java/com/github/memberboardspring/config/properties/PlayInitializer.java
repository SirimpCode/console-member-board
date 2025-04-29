package com.github.memberboardspring.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PlayInitializer {
    private final RestTemplate restTemplate;
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        System.out.println("프로그램이 실행 됩니다.");
        String url = "http://localhost:8080/api/play";
        restTemplate.getForObject(url, String.class);
    }
}
