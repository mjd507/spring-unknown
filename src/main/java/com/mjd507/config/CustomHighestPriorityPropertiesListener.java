package com.mjd507.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class CustomHighestPriorityPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Map<String, Object> properties = new HashMap<>();
        properties.put("secret", "get_from_security_api_for_real_pswd");
        MapPropertySource highestPriorityProperties = new MapPropertySource("highestPriorityProperties", properties);
        environment.getPropertySources().addFirst(highestPriorityProperties);
    }
}
