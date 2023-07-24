package com.test.webscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@Import(ScheduledSpringConfig.class)
public class WebscraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebscraperApplication.class, args);
	}

}
