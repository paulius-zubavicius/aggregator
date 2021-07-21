package com.klix.aggregator.services.rest;

import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.klix.aggregator.services.bank.adapters.BankResponse;

@Service
public class RestClientServiceImpl implements RestClientService {

	@Override
	public <BODY> BankResponse post(String url, BODY body) {
		return call(HttpMethod.POST, url, body);
	}

	@Override
	public BankResponse get(String url) {
		return call(HttpMethod.GET, url, null);
	}

	@Override
	public <BODY> BankResponse call(HttpMethod method, String url, BODY body) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", "application/json");
		HttpEntity<BODY> request = new HttpEntity<BODY>(body, headers);
		url = URLDecoder.decode(url, Charset.defaultCharset());
		ResponseEntity<BankResponse> response = new RestTemplate().exchange(url, method, request, BankResponse.class);
		return response.getBody();
	}

}
