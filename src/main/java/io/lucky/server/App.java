package io.lucky.server;

import io.lucky.server.common.Configure;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class App {
	private static final Configure conf = Configure.getInstance();

	public static void main(String[] args) {

		SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder(App.class);
		Map<String, Object> properties = new HashMap<>();

		setSystemProperties();
		setApplicationProperties(properties);

		applicationBuilder.properties(properties);
		applicationBuilder.run(args);
	}

	private static void setApplicationProperties(Map<String, Object> properties) {
		properties.put("server.port", conf.web_port);
	}

	private static void setSystemProperties() {
		System.setProperty("user.timezone", conf.gmt);
	}
}
