package com.klix.aggregator.services.application.validators;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.klix.aggregator.config.BankAdaptersConfig;
import com.klix.aggregator.config.BankAdaptersConfig.ConfigValues;

public class PhoneNumberForBanksValidator implements ConstraintValidator<PhoneNumberForBanks, String> {

	private static final String DELIMETER = "; ";
	private static final String MSG = "Not valid format. Phone number format should be one of: ";

	private BankAdaptersConfig adaptersConfig;

	@Autowired
	public PhoneNumberForBanksValidator(BankAdaptersConfig adaptersConfig) {
		this.adaptersConfig = adaptersConfig;
	}

	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {

		if (phoneNumber == null || phoneNumber.isBlank()) {
			return false;
		}

		List<String> regexps = adaptersConfig.getConfig().values().stream().map(ConfigValues::getPhoneNumberRegexp)
				.collect(Collectors.toList());

		if (!anyValid(phoneNumber, regexps)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(MSG + adaptersConfig.getConfig().values().stream()
					.map(ConfigValues::getPhoneNumberExamples).collect(Collectors.joining(DELIMETER)))
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private boolean anyValid(String phoneNumber, List<String> regexps) {
		return regexps.stream().anyMatch(regexp -> phoneNumber.matches(regexp));
	}

}