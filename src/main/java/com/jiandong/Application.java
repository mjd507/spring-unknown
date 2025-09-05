package com.jiandong;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by mjd on 2021/5/14 13:23
 */
@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.addListeners(new CustomHighestPriorityPropertiesListener());
		application.run(args);
	}

	static class CustomHighestPriorityPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

		@Override
		public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
			ConfigurableEnvironment environment = event.getEnvironment();
			Map<String, Object> properties = new HashMap<>();
			properties.put("secret", "get_from_security_api_for_real_pswd");
			MapPropertySource highestPriorityProperties = new MapPropertySource("highestPriorityProperties", properties);
			environment.getPropertySources().addFirst(highestPriorityProperties);
		}

	}

}
