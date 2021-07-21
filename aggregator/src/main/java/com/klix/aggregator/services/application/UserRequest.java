package com.klix.aggregator.services.application;

import java.math.BigDecimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.MaritalStatus;
import com.klix.aggregator.services.application.validators.PhoneNumberForBanks;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {

	@PhoneNumberForBanks
	private String phone;

	@Email
	@NotNull
	private String email;

	@PositiveOrZero
	@NotNull
	private BigDecimal monthlyIncome;

	@PositiveOrZero
	@NotNull
	private BigDecimal monthlyExpenses;

	@NotNull
	private MaritalStatus maritalStatus;

	@PositiveOrZero
	@NotNull
	private Integer dependents;

	@NotNull
	private Boolean agreeToBeScored;

	@Positive
	@NotNull
	private BigDecimal amount;

	public UserRequest(ClientRequestEntity entity) {
		this.phone = entity.getPhone();
		this.email = entity.getEmail();
		this.monthlyIncome = entity.getMonthlyIncome();
		this.monthlyExpenses = entity.getMonthlyExpenses();
		this.maritalStatus = entity.getMaritalStatus();
		this.dependents = entity.getDependents();
		this.agreeToBeScored = entity.getAgreeToBeScored();
		this.amount = entity.getAmount();

	}

}
