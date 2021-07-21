package com.klix.aggregator.services.bank.adapters.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klix.aggregator.config.BankAdaptersConfig;
import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.MaritalStatus;
import com.klix.aggregator.services.application.UserRequest;
import com.klix.aggregator.services.bank.adapters.BankAdapter;
import com.klix.aggregator.services.bank.adapters.BankResponse;
import com.klix.aggregator.services.rest.RestClientService;

import lombok.Data;

@Service
public class SolidBankAdapter implements BankAdapter {

	@Autowired
	private BankAdaptersConfig configs;

	@Autowired
	private RestClientService restClient;

	@Override
	public BankName getBankName() {
		return BankName.SolidBank;
	}

	@Override
	public BankResponse createApplication(UserRequest req) {
		return restClient.post(configs.getConfigBy(getBankName()).getUrl(), new SolidBankReq(req));
	}

	@Override
	public BankResponse getApplication(String extId) {
		return restClient.get(configs.getConfigBy(getBankName()).getUrl() + "/" + extId);
	}

	@Data
	private static class SolidBankReq {
		private String phone;
		private String email;
		private BigDecimal monthlyIncome;
		private BigDecimal monthlyExpenses;
		private MaritalStatus maritalStatus;
		private Boolean agreeToBeScored;
		private BigDecimal amount;

		public SolidBankReq(UserRequest req) {
			this.phone = req.getPhone();
			this.email = req.getEmail();
			this.monthlyIncome = req.getMonthlyIncome();
			this.monthlyExpenses = req.getMonthlyExpenses();
			this.maritalStatus = req.getMaritalStatus();
			this.agreeToBeScored = req.getAgreeToBeScored();
			this.amount = req.getAmount();
		}

	}

}
