package com.klix.aggregator.services.bank.adapters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klix.aggregator.db.model.BankName;

@Service
public class BankAdapatersServiceImpl implements BankAdapatersService {

	@Autowired
	private List<BankAdapter> bankAdapters;

	@Override
	public BankAdapter getByName(BankName name) {
		return bankAdapters.stream().filter(ad -> ad.getBankName().equals(name)).findAny().get();
	}

	@Override
	public List<BankName> getAllNames() {
		return bankAdapters.stream().map(BankAdapter::getBankName).collect(Collectors.toList());
	}

}
