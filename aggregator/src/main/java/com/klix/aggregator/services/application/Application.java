package com.klix.aggregator.services.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.klix.aggregator.db.model.BankName;

import lombok.Data;

@Data
public class Application {

	private Long id;
	private UserRequest request;
	private Instant created;
	private List<Offer> offers;

	@Data
	public static class Offer {
		private String extId;
		private BankName bank;
		private OfferStatus status;
		private BigDecimal monthlyPaymentAmount;
		private BigDecimal totalRepaymentAmount;
		private Integer numberOfPayments;
		private BigDecimal annualPercentageRate;
		private String firstRepaymentDate;

	}

	public static enum OfferStatus {
		WAITING, OK, NO_OFFER, REJECTED
	}

}
