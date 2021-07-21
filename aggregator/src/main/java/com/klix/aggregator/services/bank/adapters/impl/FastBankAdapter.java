package com.klix.aggregator.services.bank.adapters.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klix.aggregator.config.BankAdaptersConfig;
import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.services.application.UserRequest;
import com.klix.aggregator.services.bank.adapters.BankAdapter;
import com.klix.aggregator.services.bank.adapters.BankResponse;
import com.klix.aggregator.services.rest.RestClientService;

import lombok.Data;

@Service
public class FastBankAdapter implements BankAdapter {

	@Autowired
	private BankAdaptersConfig configs;

	@Autowired
	private RestClientService restClient;

	@Override
	public BankName getBankName() {
		return BankName.FastBank;
	}

	@Override
	public BankResponse createApplication(UserRequest req) {
		return restClient.post(configs.getConfigBy(getBankName()).getUrl(), new FastBankReq(req));
	}

	@Override
	public BankResponse getApplication(String extId) {
		return restClient.get(configs.getConfigBy(getBankName()).getUrl() + "/" + extId);
	}

	@Data
	private static class FastBankReq {
		private String phoneNumber;
		private String email;
		private BigDecimal monthlyIncomeAmount;
		private BigDecimal monthlyCreditLiabilities;
		private Integer dependents;
		private Boolean agreeToDataSharing;
		private BigDecimal amount;

		public FastBankReq(UserRequest req) {
			this.phoneNumber = req.getPhone();
			this.email = req.getEmail();
			this.monthlyIncomeAmount = req.getMonthlyIncome();
			this.monthlyCreditLiabilities = req.getMonthlyExpenses();
			this.dependents = req.getDependents();
			this.agreeToDataSharing = req.getAgreeToBeScored();
			this.amount = req.getAmount();
		}

	}

}
