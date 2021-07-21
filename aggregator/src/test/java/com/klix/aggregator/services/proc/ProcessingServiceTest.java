package com.klix.aggregator.services.proc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.services.application.UserRequest;
import com.klix.aggregator.services.bank.adapters.BankAdapatersService;
import com.klix.aggregator.services.bank.adapters.BankAdapter;
import com.klix.aggregator.services.bank.adapters.BankResponse;
import com.klix.aggregator.services.storage.StorageService;

@SpringBootTest
public class ProcessingServiceTest {

	@MockBean
	private BankAdapatersService bankAdapters;

	@MockBean
	private StorageService storageService;

	@Autowired
	private ProcessingService service;

	private BankAdapter bankAdapter;

	@BeforeEach
	public void init() {

		bankAdapter = Mockito.mock(BankAdapter.class);
		when(bankAdapter.createApplication(Mockito.any(UserRequest.class)))
				.then(a -> new BankResponse(RequestStatus.CREATED, BankName.FastBank));
		when(bankAdapter.getApplication(Mockito.anyString()))
				.then(a -> new BankResponse(RequestStatus.CREATED, BankName.FastBank));
		when(bankAdapter.getBankName()).then(a -> BankName.FastBank);

		when(bankAdapters.getByName(Mockito.any(BankName.class))).then(a -> bankAdapter);

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(100L);
		boEntity.setBankName(BankName.FastBank);
		when(storageService.startProcessing(Mockito.anyLong())).then(a -> boEntity);
		when(storageService.stopProcessing(Mockito.anyLong(), Mockito.any())).then(a -> boEntity);
	}

	@Test
	public void procCreatedTest() {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(100L);
		boEntity.setStatus(RequestStatus.CREATED);
		boEntity.setBankName(BankName.FastBank);
		boEntity.setClientRequest(new ClientRequestEntity());

		service.process(boEntity);

		InOrder inOrder = Mockito.inOrder(storageService, bankAdapter);
		inOrder.verify(storageService, Mockito.calls(1)).startProcessing(Mockito.eq(100L));
		inOrder.verify(bankAdapter, Mockito.calls(1)).createApplication(Mockito.any());
		inOrder.verify(storageService, Mockito.calls(1)).stopProcessing(Mockito.eq(100L), Mockito.any());

		verify(bankAdapter, Mockito.never()).getApplication(Mockito.anyString());

	}

	@ParameterizedTest
	@EnumSource(value = RequestStatus.class, names = { "DRAFT", "PROCESSED", "REJECTED" })
	public void procUpdateTest(RequestStatus status) {

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(100L);
		boEntity.setStatus(status);
		boEntity.setBankName(BankName.FastBank);
		boEntity.setClientRequest(new ClientRequestEntity());
		boEntity.setExtId("any-ext-id");

		service.process(boEntity);

		InOrder inOrder = Mockito.inOrder(storageService, bankAdapter);
		inOrder.verify(storageService, Mockito.calls(1)).startProcessing(Mockito.eq(100L));
		inOrder.verify(bankAdapter, Mockito.calls(1)).getApplication(Mockito.anyString());
		inOrder.verify(storageService, Mockito.calls(1)).stopProcessing(Mockito.eq(100L), Mockito.any());

		verify(bankAdapter, Mockito.never()).createApplication(Mockito.any());

	}

	@Test
	public void validationFailByRemoteTest() {

		// Request fails of validation
		when(bankAdapter.createApplication(Mockito.any(UserRequest.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Optional<BankOfferEntity>> boEntityCaptor = ArgumentCaptor.forClass(Optional.class);

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(100L);
		boEntity.setStatus(RequestStatus.CREATED);
		boEntity.setBankName(BankName.FastBank);
		boEntity.setClientRequest(new ClientRequestEntity());

		service.process(boEntity);

		InOrder inOrder = Mockito.inOrder(storageService, bankAdapter);
		inOrder.verify(storageService, Mockito.calls(1)).startProcessing(Mockito.eq(100L));
		inOrder.verify(bankAdapter, Mockito.calls(1)).createApplication(Mockito.any());
		inOrder.verify(storageService, Mockito.calls(1)).stopProcessing(Mockito.eq(100L), boEntityCaptor.capture());

		Optional<BankOfferEntity> capturedVal = boEntityCaptor.getValue();
		assertNotNull(capturedVal);
		assertTrue(capturedVal.isPresent());
		assertEquals(RequestStatus.REJECTED, capturedVal.get().getStatus());
		assertEquals(BankName.FastBank, capturedVal.get().getBankName());

	}

	@Test
	public void exceptionByRemoteTest() {

		// Request fails of validation
		when(bankAdapter.createApplication(Mockito.any(UserRequest.class))).thenThrow(new RuntimeException());

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Optional<BankOfferEntity>> boEntityCaptor = ArgumentCaptor.forClass(Optional.class);

		BankOfferEntity boEntity = new BankOfferEntity();
		boEntity.setId(100L);
		boEntity.setStatus(RequestStatus.CREATED);
		boEntity.setBankName(BankName.FastBank);
		boEntity.setClientRequest(new ClientRequestEntity());

		service.process(boEntity);

		InOrder inOrder = Mockito.inOrder(storageService, bankAdapter);
		inOrder.verify(storageService, Mockito.calls(1)).startProcessing(Mockito.eq(100L));
		inOrder.verify(bankAdapter, Mockito.calls(1)).createApplication(Mockito.any());
		inOrder.verify(storageService, Mockito.calls(1)).stopProcessing(Mockito.eq(100L), boEntityCaptor.capture());

		Optional<BankOfferEntity> capturedVal = boEntityCaptor.getValue();
		assertNotNull(capturedVal);
		assertTrue(capturedVal.isEmpty());

	}

}
