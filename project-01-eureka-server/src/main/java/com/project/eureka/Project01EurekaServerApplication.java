package com.project.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Project01EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Project01EurekaServerApplication.class, args);
  System.out.println("project soa eurekaServer");
    }

}
