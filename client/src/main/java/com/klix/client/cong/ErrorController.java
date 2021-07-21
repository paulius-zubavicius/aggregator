package com.klix.client.cong;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

import com.klix.client.service.errhandler.RestClientErrorHandler;

import lombok.Data;
import lombok.Value;

@ControllerAdvice
public class ErrorController {
	private static Logger LOG = LoggerFactory.getLogger(ErrorController.class);

	@Autowired
	private RestClientErrorHandler restClientErrHandler;

	@ExceptionHandler(Throwable.class)
	public String exception(Throwable e, Model model) {
		LOG.warn("Error occured", e);
		model.addAttribute("err", createErrorResponse(e, INTERNAL_SERVER_ERROR.value()));
		return "error";
	}

	@ExceptionHandler(value = { RestClientResponseException.class })
	public String restClientException(RestClientResponseException e, Model model) {
		LOG.info("Validation fail: {}", e.getMessage());
		model.addAttribute("err", restClientErrHandler.handleError(e));
		return "error";
	}

	private Errors createErrorResponse(Throwable e, int code) {
		String errorId = outputUniqId(e);

		Errors response = new Errors(code, errorId);
		response.getErrors().add(new Error(e.toString()));

		while (e.getCause() != null) {
			e = e.getCause();
			response.getErrors().add(new Error(e.toString()));
		}
		return response;
	}

	private String outputUniqId(Throwable e) {
		String errorId = UUID.randomUUID().toString();
		LOG.error("ErrorId: {}", errorId, e);
		return errorId;
	}

	@Data
	public class Errors {

		private String errorId;
		private List<Error> errors = new ArrayList<>();
		private int code;

		public Errors(int code, String errorId) {
			this.errorId = errorId;
			this.code = code;
		}

		public Errors(int code, List<Error> errors) {
			this.errors.addAll(errors);
			this.errorId = null;
			this.code = code;
		}
	}

	@Value
	public class Error {
		private String field;
		private String msg;

		public Error(ObjectError err) {
			this.msg = err.getDefaultMessage();
			this.field = ((DefaultMessageSourceResolvable) err.getArguments()[0]).getDefaultMessage();
		}

		public Error(String violation) {
			this.field = null;
			this.msg = violation;
		}
	}

}
