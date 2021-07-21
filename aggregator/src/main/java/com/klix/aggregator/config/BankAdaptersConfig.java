package com.klix.aggregator.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.klix.aggregator.db.model.BankName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "banks")
@Data
public class BankAdaptersConfig {

	private Map<BankName, ConfigValues> config = new HashMap<>();

	public ConfigValues getConfigBy(BankName bankName) {
		return config.get(bankName);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ConfigValues {
		private String url;
		private String phoneNumberRegexp;
		private String phoneNumberExamples;

		// Place for other bank specified attributes
	}

}
