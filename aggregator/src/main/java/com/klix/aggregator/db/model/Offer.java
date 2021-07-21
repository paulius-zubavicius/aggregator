package com.klix.aggregator.db.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class Offer {
	
	@Column(name = "offerMonthlyPayment")
	private BigDecimal monthlyPayment;
	
	@Column(name = "offerTotalRepayment")
	private BigDecimal totalRepayment;
	
	@Column(name = "offerNumberOfPayments")
	private Integer numberOfPayments;
	
	@Column(name = "offerAnnualRate")
	private BigDecimal annualRate;
	
	@Column(name = "offerFirstRepayment")
	private String firstRepayment;

}
