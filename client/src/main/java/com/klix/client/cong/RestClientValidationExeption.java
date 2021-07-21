package com.klix.client.cong;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

import lombok.Getter;

public class RestClientValidationExeption extends RestClientResponseException {

	private static final long serialVersionUID = 1L;

	@Getter
	private String returnMvcView;

	public RestClientValidationExeption(String message, int statusCode, String statusText, HttpHeaders responseHeaders,
			byte[] responseBody, Charset responseCharset, String returnMvcView) {
		super(message, statusCode, statusText, responseHeaders, responseBody, responseCharset);
		this.returnMvcView = returnMvcView;
	}

}
