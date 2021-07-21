package com.klix.client.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LoanRequest {

	private String phone;
	private String email;
	private BigDecimal monthlyIncome;
	private BigDecimal monthlyExpenses;
	private MaritalStatus maritalStatus;
	private Integer dependents = 0;
	private Boolean agreeToBeScored;
	private BigDecimal amount;
}
