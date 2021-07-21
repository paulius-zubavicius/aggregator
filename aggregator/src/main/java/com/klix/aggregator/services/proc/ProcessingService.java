package com.klix.aggregator.services.proc;

import com.klix.aggregator.db.model.BankOfferEntity;

public interface ProcessingService {

	void process(BankOfferEntity offerEntity);

}
