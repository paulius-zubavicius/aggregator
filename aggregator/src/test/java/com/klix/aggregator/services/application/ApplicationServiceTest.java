package com.klix.aggregator.services.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.MaritalStatus;
import com.klix.aggregator.db.model.Offer;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.services.application.Application.OfferStatus;
import com.klix.aggregator.services.bank.adapters.BankAdapatersService;
import com.klix.aggregator.services.storage.StorageService;

@SpringBootTest
public class ApplicationServiceTest {

	@MockBean
	private BankAdapatersService bankAdapters;

	@MockBean
	private StorageService storageService;

	@Autowired
	private ApplicationService service;

	private List<BankName> banks = Arrays.asList(BankName.FastBank, BankName.SolidBank);
	private ClientRequestEntity getByIdEntity = null;

	@BeforeEach
	public void init() {

		when(bankAdapters.getAllNames()).then(a -> banks);

		when(storageService.create(Mockito.any(ClientRequestEntity.class), Mockito.anyList()))
				.then(new Answer<ClientRequestEntity>() {

					@Override
					public ClientRequestEntity answer(InvocationOnMock invocation) throws Throwable {
						ClientRequestEntity entity = invocation.getArgument(0, ClientRequestEntity.class);
						@SuppressWarnings("unchecked")
						List<BankOfferEntity> offersDrafts = invocation.getArgument(1, List.class);
						entity.setId(99L);
						entity.setResponses(offersDrafts);
						offersDrafts.forEach(o -> o.setClientRequest(entity));
						return entity;
					}
				});

		when(storageService.findById(Mockito.anyLong())).then(a -> getByIdEntity);
	}

	@Test
	public void createUserRequest() {

		banks = Arrays.asList(BankName.FastBank);

		UserRequest req = new UserRequest();
		req.setAgreeToBeScored(true);
		req.setAmount(BigDecimal.ONE);
		req.setDependents(0);
		req.setEmail("email@email.email");
		req.setMaritalStatus(MaritalStatus.SINGLE);
		req.setMonthlyExpenses(BigDecimal.ONE);
		req.setMonthlyIncome(BigDecimal.ONE);
		req.setPhone("+37166600000");

		Application app = service.create(req);

		assertEquals(99L, app.getId());
		assertNotNull(app.getCreated());

		assertNotNull(app.getRequest());
		assertEquals(req.getAgreeToBeScored(), app.getRequest().getAgreeToBeScored());
		assertEquals(req.getAmount(), app.getRequest().getAmount());
		assertEquals(req.getDependents(), app.getRequest().getDependents());
		assertEquals(req.getEmail(), app.getRequest().getEmail());
		assertEquals(req.getMaritalStatus(), app.getRequest().getMaritalStatus());
		assertEquals(req.getMonthlyExpenses(), app.getRequest().getMonthlyExpenses());
		assertEquals(req.getMonthlyIncome(), app.getRequest().getMonthlyIncome());
		assertEquals(req.getPhone(), app.getRequest().getPhone());

		assertNotNull(app.getOffers());
		assertEquals(banks.size(), app.getOffers().size());

		// No external call
		assertNull(app.getOffers().get(0).getExtId());
		assertNull(app.getOffers().get(0).getAnnualPercentageRate());
		assertNull(app.getOffers().get(0).getFirstRepaymentDate());
		assertNull(app.getOffers().get(0).getMonthlyPaymentAmount());
		assertNull(app.getOffers().get(0).getTotalRepaymentAmount());

		assertEquals(BankName.FastBank, app.getOffers().get(0).getBank());
		assertEquals(OfferStatus.WAITING, app.getOffers().get(0).getStatus());

	}

	@Test
	public void statusMap_CREATED_Test() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(1L);
		boEntity.setExtId(null);
		boEntity.setCreated(Instant.now());
		boEntity.setBankName(BankName.FastBank);
		boEntity.setStatus(RequestStatus.CREATED);
		boEntity.setOffer(new Offer());

		getByIdEntity = new ClientRequestEntity();
		getByIdEntity.setResponses(new ArrayList<>());
		getByIdEntity.getResponses().add(boEntity);

		Application app = service.findById(99L);

		assertNotNull(app.getOffers());
		assertEquals(1, app.getOffers().size());
		assertEquals(OfferStatus.WAITING, app.getOffers().get(0).getStatus());

	}

	@Test
	public void statusMap_DRAF_Test() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(1L);
		boEntity.setExtId(null);
		boEntity.setCreated(Instant.now());
		boEntity.setBankName(BankName.FastBank);
		boEntity.setStatus(RequestStatus.DRAFT);
		boEntity.setOffer(new Offer());

		getByIdEntity = new ClientRequestEntity();
		getByIdEntity.setResponses(new ArrayList<>());
		getByIdEntity.getResponses().add(boEntity);

		Application app = service.findById(99L);

		assertNotNull(app.getOffers());
		assertEquals(1, app.getOffers().size());
		assertEquals(OfferStatus.WAITING, app.getOffers().get(0).getStatus());

	}

	@Test
	public void statusMap_PROCESSED_OK_Test() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(1L);
		boEntity.setExtId("any-uniq-id");
		boEntity.setCreated(Instant.now());
		boEntity.setBankName(BankName.FastBank);
		boEntity.setStatus(RequestStatus.PROCESSED);
		boEntity.setOffer(new Offer());
		boEntity.getOffer().setAnnualRate(BigDecimal.ONE);
		boEntity.getOffer().setFirstRepayment("first repayment");
		boEntity.getOffer().setMonthlyPayment(BigDecimal.TEN);
		boEntity.getOffer().setNumberOfPayments(1);
		boEntity.getOffer().setTotalRepayment(BigDecimal.TEN);

		getByIdEntity = new ClientRequestEntity();
		getByIdEntity.setResponses(new ArrayList<>());
		getByIdEntity.getResponses().add(boEntity);

		Application app = service.findById(99L);

		assertNotNull(app.getOffers());
		assertEquals(1, app.getOffers().size());
		assertEquals(OfferStatus.OK, app.getOffers().get(0).getStatus());
		assertEquals(boEntity.getOffer().getAnnualRate(), app.getOffers().get(0).getAnnualPercentageRate());
		assertEquals(boEntity.getOffer().getFirstRepayment(), app.getOffers().get(0).getFirstRepaymentDate());
		assertEquals(boEntity.getOffer().getMonthlyPayment(), app.getOffers().get(0).getMonthlyPaymentAmount());
		assertEquals(boEntity.getOffer().getNumberOfPayments(), app.getOffers().get(0).getNumberOfPayments());
		assertEquals(boEntity.getOffer().getTotalRepayment(), app.getOffers().get(0).getTotalRepaymentAmount());

	}

	@Test
	public void statusMap_PROCESSED_NO_OFFER_Test() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(1L);
		boEntity.setExtId("any-uniq-id");
		boEntity.setCreated(Instant.now());
		boEntity.setBankName(BankName.FastBank);
		boEntity.setStatus(RequestStatus.PROCESSED);
		boEntity.setOffer(new Offer());

		getByIdEntity = new ClientRequestEntity();
		getByIdEntity.setResponses(new ArrayList<>());
		getByIdEntity.getResponses().add(boEntity);

		Application app = service.findById(99L);

		assertNotNull(app.getOffers());
		assertEquals(1, app.getOffers().size());
		assertEquals(OfferStatus.NO_OFFER, app.getOffers().get(0).getStatus());

	}

	@Test
	public void statusMap_REJECTED_Test() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(1L);
		boEntity.setExtId(null);
		boEntity.setCreated(null);
		boEntity.setBankName(BankName.FastBank);
		boEntity.setStatus(RequestStatus.REJECTED);
		boEntity.setOffer(new Offer());

		getByIdEntity = new ClientRequestEntity();
		getByIdEntity.setResponses(new ArrayList<>());
		getByIdEntity.getResponses().add(boEntity);

		Application app = service.findById(99L);

		assertNotNull(app.getOffers());
		assertEquals(1, app.getOffers().size());
		assertEquals(OfferStatus.REJECTED, app.getOffers().get(0).getStatus());

	}

}
