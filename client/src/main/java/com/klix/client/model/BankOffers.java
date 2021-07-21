package com.klix.client.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class BankOffers {

	private Long id;
	private LoanRequest request;
	private Instant created;
	private List<BankOffer> offers;

	@Data
	public static class BankOffer {
		private String extId;
		private String bank;
		private OfferStatus status;
		private BigDecimal monthlyPaymentAmount;
		private BigDecimal totalRepaymentAmount;
		private Integer numberOfPayments;
		private BigDecimal annualPercentageRate;
		private String firstRepaymentDate;
	}

	public boolean isWaitingForResponse() {
		if (offers != null) {
			return offers.stream().map(BankOffer::getStatus).anyMatch(OfferStatus.WAITING::equals);
		}
		return false;
	}

	public static enum OfferStatus {
		WAITING, OK, NO_OFFER, REJECTED
	}

}
