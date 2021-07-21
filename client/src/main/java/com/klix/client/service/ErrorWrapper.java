package com.klix.client.service;

import com.klix.client.cong.ErrorController.Errors;

import lombok.Getter;

@Getter
public class ErrorWrapper<R> {

	private R response;

	private Errors errors;

	public ErrorWrapper(R response) {
		this.response = response;
	}

	public ErrorWrapper(Errors errors) {
		this.errors = errors;
	}

}
