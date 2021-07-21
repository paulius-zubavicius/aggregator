package com.klix.client.cong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Value("${refresh.interval:5}")
	private int refreshInterval;

	@Bean("refreshInterval")
	public int getRefreshInterval() {
		return refreshInterval;
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new InstanceFormatter());
	}

}
