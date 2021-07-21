package com.klix.aggregator.services.application;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.ProcessStatus;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.services.application.Application.Offer;
import com.klix.aggregator.services.application.Application.OfferStatus;
import com.klix.aggregator.services.bank.adapters.BankAdapatersService;
import com.klix.aggregator.services.storage.StorageService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private BankAdapatersService bankAdapters;

	@Autowired
	private StorageService storageService;

	@Override
	public Application create(UserRequest req) {

		ClientRequestEntity reqEntity = toAppRequestEntity(req);

		List<BankOfferEntity> offersEntities = bankAdapters.getAllNames().stream().map(this::toBankOfferEntity)
				.collect(Collectors.toList());

		return toBankResponse(storageService.create(reqEntity, offersEntities));
	}

	@Override
	public Application findById(Long id) {
		return toBankResponse(storageService.findById(id));
	}

	private Application toBankResponse(ClientRequestEntity reqEntity) {
		Application result = new Application();
		result.setId(reqEntity.getId());
		result.setRequest(new UserRequest(reqEntity));
		result.setCreated(reqEntity.getCreated());
		result.setOffers(reqEntity.getResponses().stream().map(this::toOffer).collect(Collectors.toList()));
		return result;
	}

	private BankOfferEntity toBankOfferEntity(BankName bankName) {
		BankOfferEntity result = new BankOfferEntity();
		result.setBankName(bankName);
		result.setCreated(Instant.now());
		result.setStatus(RequestStatus.CREATED);
		result.setStatusProc(ProcessStatus.READY);
		return result;
	}

	private Offer toOffer(BankOfferEntity entity) {
		Offer result = new Offer();
		result.setExtId(entity.getExtId());
		result.setBank(entity.getBankName());

		if (entity.getOffer() != null) {
			result.setMonthlyPaymentAmount(entity.getOffer().getMonthlyPayment());
			result.setTotalRepaymentAmount(entity.getOffer().getTotalRepayment());
			result.setNumberOfPayments(entity.getOffer().getNumberOfPayments());
			result.setAnnualPercentageRate(entity.getOffer().getAnnualRate());
			result.setFirstRepaymentDate(entity.getOffer().getFirstRepayment());
		}

		result.setStatus(statusRemap(entity));

		return result;
	}

	private OfferStatus statusRemap(BankOfferEntity entity) {

		if (RequestStatus.CREATED.equals(entity.getStatus()) || RequestStatus.DRAFT.equals(entity.getStatus())) {
			return OfferStatus.WAITING;
		}

		if (RequestStatus.REJECTED.equals(entity.getStatus())) {
			return OfferStatus.REJECTED;
		}

		if (RequestStatus.PROCESSED.equals(entity.getStatus())) {
			if (entity.getOffer() != null) {
				boolean empty = true;
				if (entity.getOffer() != null) {
					empty &= entity.getOffer().getMonthlyPayment() == null;
					empty &= entity.getOffer().getTotalRepayment() == null;
					empty &= entity.getOffer().getNumberOfPayments() == null;
					empty &= entity.getOffer().getAnnualRate() == null;
					empty &= entity.getOffer().getFirstRepayment() == null;
				}

				if (!empty) {
					return OfferStatus.OK;
				}
			}
			return OfferStatus.NO_OFFER;
		}

		throw new RuntimeException("No case for : " + entity.getStatus());
	}

	private ClientRequestEntity toAppRequestEntity(UserRequest req) {
		ClientRequestEntity result = new ClientRequestEntity();
		result.setCreated(Instant.now());
		result.setPhone(req.getPhone());
		result.setEmail(req.getEmail());
		result.setMonthlyIncome(req.getMonthlyIncome());
		result.setMonthlyExpenses(req.getMonthlyExpenses());
		result.setMaritalStatus(req.getMaritalStatus());
		result.setDependents(req.getDependents());
		result.setAgreeToBeScored(req.getAgreeToBeScored());
		result.setAmount(req.getAmount());
		return result;
	}

}
