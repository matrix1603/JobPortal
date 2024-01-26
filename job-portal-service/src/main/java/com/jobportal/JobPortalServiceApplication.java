package com.jobportal;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@OpenAPIDefinition(info = @Info(title = "Job Portal Service", description = "Job Portal Service Backend Application", version = "1.0.0"))
@SpringBootApplication
@ComponentScan(basePackages = {"controllers.*", "services.*", "exceptionHandlers.*", "config.*", "producers.*", "utility.jwt"})
@EnableJpaRepositories(basePackages = "repository.*")
@EntityScan(basePackages = {"entity.*"})
public class JobPortalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalServiceApplication.class, args);
    }

}
