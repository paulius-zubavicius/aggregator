package com.klix.aggregator.services.rest;

import org.springframework.http.HttpMethod;

import com.klix.aggregator.services.bank.adapters.BankResponse;

public interface RestClientService {

	public <BODY> BankResponse post(String url, BODY body) ;

	public BankResponse get(String url) ;

	public <BODY> BankResponse call(HttpMethod method, String url, BODY body);

}
