package com.klix.client.service.errhandler;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import com.google.gson.Gson;
import com.klix.client.cong.ErrorController.Errors;
import com.klix.client.service.ErrorWrapper;

@Service
public class RestClientErrorHandler {

	public <R> ErrorWrapper<R> wrapException(Supplier<R> sup, Predicate<RestClientResponseException> predicate) {

		try {
			return new ErrorWrapper<>(sup.get());
		} catch (RestClientResponseException e) {
			if (predicate.test(e)) {
				return new ErrorWrapper<>(handleError(e));
			}
			throw e;
		}
	}

	public Errors handleError(RestClientResponseException e) {
		Gson gson = new Gson();
		Errors err = gson.fromJson(e.getResponseBodyAsString(), Errors.class);
		err.setCode(e.getRawStatusCode());
		return err;
	}

}
