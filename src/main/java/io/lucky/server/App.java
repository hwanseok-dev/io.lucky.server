package io.lucky.server;

import io.lucky.server.common.Configure;
import io.lucky.server.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class App {
	private static Configure conf;
	public static void main(String[] args) {
		SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder(App.class);
		Map<String, Object> properties = new HashMap<>();
		App.conf = Configure.getInstance();
		setSystemProperties();
		setApplicationProperties(properties);

		applicationBuilder.properties(properties);
		applicationBuilder.run(args);
	}

	private static void setApplicationProperties(Map<String, Object> properties) {
		properties.put("server.port", conf.web_port);
		properties.put("spring.datasource.url", conf.db_url);
		properties.put("spring.datasource.username", conf.db_username);
		properties.put("spring.datasource.password", conf.db_password);
		log.info("spring.datasource.url : {}", conf.db_url);
		log.info("spring.datasource.username : {}", conf.db_username);
		log.info("spring.datasource.password : {}", conf.db_password);

	}

	private static void setSystemProperties() {
		System.setProperty("user.timezone", conf.gmt);
//		System.setProperty("lucky.conf.path", conf.getConfPath());
	}
}
