package com.klix.aggregator.db.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import lombok.Data;

@Entity(name = "ClientRequest")
@Data
public class ClientRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Integer version;

	private Instant created;
	private String phone;
	private String email;
	private BigDecimal monthlyIncome;
	private BigDecimal monthlyExpenses;

	@Enumerated(EnumType.STRING)
	private MaritalStatus maritalStatus;
	private Integer dependents;
	private Boolean agreeToBeScored;
	private BigDecimal amount;

	@OneToMany(mappedBy = "clientRequest", cascade = CascadeType.ALL)
	private List<BankOfferEntity> responses = new ArrayList<>();

}
