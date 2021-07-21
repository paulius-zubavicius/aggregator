package com.klix.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.klix.client.model.BankOffers;
import com.klix.client.model.LoanRequest;
import com.klix.client.service.errhandler.RestClientErrorHandler;

@Service
public class LoanRequestServiceImpl implements LoanRequestService {

	private static final int VALIDATION = 400;

	@Value("${service.aggregator.url}")
	private String url;

	@Autowired
	private RestClientErrorHandler restClientErrHandler;

	@Override
	public BankOffers getOffers(String id) {
		return new RestTemplate().getForObject(url + "/" + id, BankOffers.class);
	}

	@Override
	public ErrorWrapper<BankOffers> createRequest(LoanRequest lreq) {
		return restClientErrHandler.wrapException(
				() -> new RestTemplate().postForEntity(url, lreq, BankOffers.class).getBody(),
				e -> e.getRawStatusCode() == VALIDATION);
	}

}
