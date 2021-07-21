package com.klix.client.service;

import com.klix.client.model.BankOffers;
import com.klix.client.model.LoanRequest;

public interface LoanRequestService {

	BankOffers getOffers(String id);

	ErrorWrapper<BankOffers> createRequest(LoanRequest lreq);

}
