package com.klix.aggregator.services.bank.adapters;

import com.klix.aggregator.db.model.BankName;
import com.klix.aggregator.services.application.UserRequest;

public interface BankAdapter {

	BankName getBankName();

	BankResponse createApplication(UserRequest req);

	BankResponse getApplication(String extId);


}
