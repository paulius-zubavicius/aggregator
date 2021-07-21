package com.klix.aggregator.services.application.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.klix.aggregator.config.BankAdaptersConfig;
import com.klix.aggregator.config.BankAdaptersConfig.ConfigValues;
import com.klix.aggregator.db.model.BankName;

public class PhoneNumberValidatorTest {

	private PhoneNumberForBanksValidator validator;
	private Map<BankName, ConfigValues> configMap = new HashMap<>();
	private ConstraintValidatorContext context;
	private BankAdaptersConfig adaptersConfig;

	@BeforeEach
	public void init() {
		configMap.clear();
		adaptersConfig = Mockito.mock(BankAdaptersConfig.class);
		when(adaptersConfig.getConfig()).then(a -> configMap);
		context = Mockito.mock(ConstraintValidatorContext.class);
		ConstraintValidatorContext.ConstraintViolationBuilder builder = Mockito
				.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
		when(context.buildConstraintViolationWithTemplate(Mockito.any())).then(s -> builder);
		validator = new PhoneNumberForBanksValidator(adaptersConfig);

	}

	@Test
	public void validFormats() {

		configMap.put(BankName.FastBank, new ConfigValues(null, "12345", null));
		configMap.put(BankName.SolidBank, new ConfigValues(null, "67890", null));

		assertTrue(validator.isValid("12345", context));
		assertTrue(validator.isValid("67890", context));
	}

	@Test
	public void notValidFormats() {

		configMap.put(BankName.FastBank, new ConfigValues(null, "12345", null));
		configMap.put(BankName.SolidBank, new ConfigValues(null, "67890", null));

		assertFalse(validator.isValid(null, context));
		assertFalse(validator.isValid(" ", context));
		assertFalse(validator.isValid("aaa", context));

		assertFalse(validator.isValid("1234", context));
		assertFalse(validator.isValid("7890", context));
		assertFalse(validator.isValid("1234567890", context));

	}

	@Test
	public void validPhoneNumberFormats() {

		configMap.put(BankName.FastBank, new ConfigValues(null, "\\+371[0-9]{8}", null));
		configMap.put(BankName.SolidBank, new ConfigValues(null, "\\+[0-9]{11,15}", null));

		assertTrue(validator.isValid("+000000123451", context));
		assertTrue(validator.isValid("+37100000000", context));
		assertTrue(validator.isValid("+333333333333333", context));
	}

	@Test
	public void notValidPhoneNumberFormats() {

		configMap.put(BankName.FastBank, new ConfigValues(null, "\\+371[0-9]{8}", null));
		configMap.put(BankName.SolidBank, new ConfigValues(null, "\\+[0-9]{11,15}", null));

		assertFalse(validator.isValid(null, context));
		assertFalse(validator.isValid(" ", context));
		assertFalse(validator.isValid("aaa", context));
		assertFalse(validator.isValid("37100000000", context));
		assertFalse(validator.isValid("+3710000000000000", context));
		assertFalse(validator.isValid("+3710000000", context));
		assertFalse(validator.isValid("+371 000 12345", context));

	}
}
