package com.klix.aggregator.services.storage;

import java.util.List;
import java.util.Optional;

import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ClientRequestEntity;
import com.klix.aggregator.db.model.ProcessStatus;
import com.klix.aggregator.db.model.RequestStatus;

public interface StorageService {

	ClientRequestEntity create(ClientRequestEntity reqEntity, List<BankOfferEntity> offersDrafts);

	ClientRequestEntity findById(Long id);

	List<BankOfferEntity> findByStatuses(ProcessStatus procStatus, List<RequestStatus> asList);

	BankOfferEntity startProcessing(Long id);

	BankOfferEntity stopProcessing(Long id, Optional<BankOfferEntity> update);

}
