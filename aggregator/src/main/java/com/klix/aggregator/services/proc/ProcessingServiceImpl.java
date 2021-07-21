package com.klix.aggregator.services.proc;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.Offer;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.services.application.UserRequest;
import com.klix.aggregator.services.bank.adapters.BankAdapatersService;
import com.klix.aggregator.services.bank.adapters.BankAdapter;
import com.klix.aggregator.services.bank.adapters.BankResponse;
import com.klix.aggregator.services.storage.StorageService;

@Service
public class ProcessingServiceImpl implements ProcessingService {

	private static Logger LOG = LoggerFactory.getLogger(ProcessingServiceImpl.class);

	private static final int HTTP_400 = 400;
	private static final int HTTP_500 = 500;

	@Autowired
	private BankAdapatersService bankAdapters;

	@Autowired
	private StorageService storageService;

	@Override
	public void process(BankOfferEntity offerEntity) {

		LOG.info("Start offerId: {}", offerEntity.getId());

		if (!lockResource(offerEntity)) {
			return;
		}

		BankAdapter adapter = bankAdapters.getByName(offerEntity.getBankName());
		Optional<BankResponse> response = callExt(adapter, offerEntity);

		if (unlockAndUpdate(offerEntity, response.map(this::toBankOfferEntity))) {
			LOG.info("Done offerId: {}", offerEntity.getId());
		}
	}

	private boolean unlockAndUpdate(BankOfferEntity offerEntity, Optional<BankOfferEntity> entity) {
		try {
			offerEntity = storageService.stopProcessing(offerEntity.getId(), entity);
		} catch (OptimisticLockException e) {
			LOG.info("Processing ok with fail. Other process changed the record: {} {}", offerEntity.getId(),
					e.getMessage());
			return false;
		} catch (Exception e) {
			LOG.error("Processing ok with fail. OfferId: {}", offerEntity.getId());
			LOG.error("Processed but status unchanged", e);
			return false;
		}
		return true;
	}

	private boolean lockResource(BankOfferEntity offerEntity) {
		try {
			offerEntity = storageService.startProcessing(offerEntity.getId());
		} catch (OptimisticLockException e) {
			LOG.info("Skipping offerId: {} {}", offerEntity.getId(), e.getMessage());
			return false;
		} catch (Exception e) {
			LOG.error("Cant process offerId: {}", offerEntity.getId());
			LOG.error("Failed to change status", e);
			return false;
		}
		return true;
	}

	private Optional<BankResponse> callExt(BankAdapter adapter, BankOfferEntity req) {

		try {

			if (RequestStatus.CREATED.equals(req.getStatus())) {
				return Optional.of(adapter.createApplication(new UserRequest(req.getClientRequest())));
			}
			return Optional.of(adapter.getApplication(req.getExtId()));

		} catch (HttpClientErrorException e) {
			return rejectedByServer(adapter, e);
		} catch (Exception e) {
			LOG.warn("Failed on ext call", e);
			return Optional.empty();
		}

	}

	private Optional<BankResponse> rejectedByServer(BankAdapter addapter, HttpClientErrorException e) {

		int httpCode = e.getRawStatusCode() - e.getRawStatusCode() % 100;

		if (httpCode == HTTP_500) {
			LOG.warn("Ext service temporary unavailable", e);
			// Do nothing. It will call again
		} else if (httpCode == HTTP_400) {
			LOG.warn("User pass invalid data: {}", e.getMessage());
			// Mark as failed case, no more calls
			return Optional.of(new BankResponse(RequestStatus.REJECTED, addapter.getBankName()));
		}

		return Optional.empty();
	}

	private BankOfferEntity toBankOfferEntity(BankResponse response) {

		BankOfferEntity result = new BankOfferEntity();
		result.setExtId(response.getId());
		result.setStatus(response.getStatus());
		result.setBankName(response.getBankName());
		result.setCreated(Instant.now());

		if (response.getOffer() != null) {
			Offer offer = new Offer();
			offer.setMonthlyPayment(response.getOffer().getMonthlyPaymentAmount());
			offer.setTotalRepayment(response.getOffer().getTotalRepaymentAmount());
			offer.setNumberOfPayments(response.getOffer().getNumberOfPayments());
			offer.setAnnualRate(response.getOffer().getAnnualPercentageRate());
			offer.setFirstRepayment(response.getOffer().getFirstRepaymentDate());
			result.setOffer(offer);
		}

		return result;
	}

}
