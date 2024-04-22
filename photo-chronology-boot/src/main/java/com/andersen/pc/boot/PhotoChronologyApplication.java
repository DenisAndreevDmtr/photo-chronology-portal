package com.andersen.pc.boot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import static com.andersen.pc.common.constant.Constant.Configuration.PACKAGES_TO_SCAN;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "Photo chronology API", version = "3.0.0", description = "Photo chronology API"))
@ComponentScan(PACKAGES_TO_SCAN)
public class PhotoChronologyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoChronologyApplication.class, args);
    }

}
