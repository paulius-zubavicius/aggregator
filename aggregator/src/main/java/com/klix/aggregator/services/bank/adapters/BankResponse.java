package com.klix.aggregator.services.bank.adapters;

import java.math.BigDecimal;

import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.RequestStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankResponse {

	private String id;
	private RequestStatus status;
	private BankName bankName;
	private Offer offer;

	public BankResponse(RequestStatus status, BankName bankName) {
		this.status = status;
		this.bankName = bankName;
	}

	@Data
	public static class Offer {
		private BigDecimal monthlyPaymentAmount;
		private BigDecimal totalRepaymentAmount;
		private Integer numberOfPayments;
		private BigDecimal annualPercentageRate;
		private String firstRepaymentDate;
	}

}
