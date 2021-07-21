package com.klix.aggregator.services.bank.adapters;

import java.util.List;

import com.klix.aggregator.db.model.BankName;

public interface BankAdapatersService {

	BankAdapter getByName(BankName name);

	List<BankName> getAllNames();

}
