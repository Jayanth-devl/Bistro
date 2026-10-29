package com.project.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.project.order.dto.MenuResponse;

@Component
public class MenuClient {

    private final RestTemplate restTemplate;

    public MenuClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MenuResponse getMenuById(Long menuId) {
        return restTemplate.getForObject(
                "http://PROJECT-03-MENU-SERVICE/api/menus/{id}",
                MenuResponse.class,
                menuId
        );
    }
}