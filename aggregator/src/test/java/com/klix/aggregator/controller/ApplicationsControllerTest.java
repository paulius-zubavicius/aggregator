package com.klix.aggregator.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.klix.aggregator.config.BankAdaptersConfig;
import com.klix.aggregator.config.BankAdaptersConfig.ConfigValues;
import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.MaritalStatus;
import com.klix.aggregator.services.application.Application;
import com.klix.aggregator.services.application.ApplicationService;
import com.klix.aggregator.services.application.UserRequest;

@WebMvcTest(controllers = { ApplicationsController.class })
public class ApplicationsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ApplicationService applications;

	@MockBean
	private BankAdaptersConfig adaptersConfig;

	private Map<BankName, ConfigValues> configMap = new HashMap<>();

	private Application responseApp;


	@BeforeEach
	private void init() {
		configMap.clear();
		configMap.put(BankName.FastBank, new ConfigValues(null, "\\+371[0-9]{8}", "FastBank phone number"));
		configMap.put(BankName.SolidBank, new ConfigValues(null, "\\+[0-9]{11,15}", "SolidBank phone number"));
		when(adaptersConfig.getConfig()).then(a -> configMap);

		responseApp = new Application();
		when(applications.create(Mockito.any())).then(a -> responseApp);
	}

	@Test
	public void postValidRequest() throws Exception {

		UserRequest req = new UserRequest();
		req.setAgreeToBeScored(true);
		req.setAmount(new BigDecimal("1000"));
		req.setDependents(3);
		req.setEmail("em@em.em");
		req.setMaritalStatus(MaritalStatus.MARRIED);
		req.setMonthlyExpenses(new BigDecimal("300"));
		req.setMonthlyIncome(new BigDecimal("2500"));
		req.setPhone("+37100099999");

		responseApp.setId(99L);

		mockMvc.perform(post("/").header("Content-Type", APPLICATION_JSON).content(new Gson().toJson(req)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(99)).andExpect(jsonPath("$.offers").hasJsonPath())
				.andExpect(jsonPath("$.request").hasJsonPath()).andExpect(jsonPath("$.created").hasJsonPath());

		verify(applications, Mockito.times(1)).create(req);
	}

	@Test
	public void postNotValidRequest() throws Exception {

		UserRequest req = new UserRequest();
		req.setAgreeToBeScored(null);
		req.setAmount(null);
		req.setDependents(null);
		req.setEmail(null);
		req.setMaritalStatus(null);
		req.setMonthlyExpenses(null);
		req.setMonthlyIncome(null);
		req.setPhone(null);

		mockMvc.perform(post("/").header("Content-Type", APPLICATION_JSON).content(new Gson().toJson(req)))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$.errorId").hasJsonPath()).andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.errors").hasJsonPath()).andExpect(jsonPath("$.errors.length()").value(8))
				.andExpect(jsonPath("$.errors[?(@.field == 'phone')].msg").value("Incorrect phone number format"))
				.andExpect(jsonPath("$.errors[?(@.field == 'dependents')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'agreeToBeScored')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'maritalStatus')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'monthlyIncome')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'email')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'amount')].msg").value("must not be null"))
				.andExpect(jsonPath("$.errors[?(@.field == 'monthlyExpenses')].msg").value("must not be null"));

		verify(applications, Mockito.never()).create(req);

	}

}
