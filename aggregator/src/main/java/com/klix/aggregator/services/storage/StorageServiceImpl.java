package com.klix.aggregator.services.storage;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.ProcessStatus;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.db.repositories.IBankOfferRepository;
import com.klix.aggregator.db.repositories.IClientRequestRepository;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {

	@Autowired
	private IClientRequestRepository clientReqRepo;

	@Autowired
	private IBankOfferRepository bankOfferRepo;

	@Override
	public ClientRequestEntity create(ClientRequestEntity reqEntity, List<BankOfferEntity> offersDrafts) {

		reqEntity.setResponses(offersDrafts);
		offersDrafts.forEach(o -> o.setClientRequest(reqEntity));

		return clientReqRepo.save(reqEntity);
	}

	@Override
	public ClientRequestEntity findById(Long id) {
		return clientReqRepo.getById(id);
	}

	@Override
	public List<BankOfferEntity> findByStatuses(ProcessStatus procStat, List<RequestStatus> statuses) {
		return bankOfferRepo.findByStatusProcAndStatusIn(procStat, statuses);
	}

	@Override
	public BankOfferEntity startProcessing(Long id) {
		BankOfferEntity entity = bankOfferRepo.getById(id);
		entity.setStatusProc(ProcessStatus.PROCESSING);
		return entity;
	}

	@Override
	public BankOfferEntity stopProcessing(Long id, Optional<BankOfferEntity> updateOpt) {

		BankOfferEntity entity = bankOfferRepo.getById(id);

		if (updateOpt.isPresent()) {
			BankOfferEntity update = updateOpt.get();
			entity.setExtId(update.getExtId());
			entity.setStatus(update.getStatus());
			if (update.getOffer() != null) {
				entity.setOffer(update.getOffer());
			}
		} else {
			entity.setStatus(RequestStatus.REJECTED);
		}
		entity.setStatusProc(ProcessStatus.READY);
		return entity;
	}

}
