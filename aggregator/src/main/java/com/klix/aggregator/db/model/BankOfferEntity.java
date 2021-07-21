package com.klix.aggregator.db.model;

import java.time.Instant;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Data;

@Entity(name = "BankOffer")
@Data
public class BankOfferEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Integer version;

	private Instant created;

	private String extId;

	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	@Enumerated(EnumType.STRING)
	private ProcessStatus statusProc;

	@Enumerated(EnumType.STRING)
	private BankName bankName;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private ClientRequestEntity clientRequest;

	@Embedded
	private Offer offer;

}
