package com.klix.aggregator.config;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.Value;

@ControllerAdvice
public class ErrorController {
	private static Logger LOG = LoggerFactory.getLogger(ErrorController.class);

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<?> exception(Throwable e) {
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(createErrorResponse(e, INTERNAL_SERVER_ERROR.value()));
	}

	// From JPA
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> notFoundById(EntityNotFoundException e) {
		return ResponseEntity.status(NOT_FOUND).body(createErrorResponse(e, NOT_FOUND.value()));
	}

	// For @Validated
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
		LOG.info("Validation exception: {}", e.getMessage());

		Errors validations = new Errors(BAD_REQUEST.value(),
				e.getConstraintViolations().stream().map(Error::new).collect(toList()));
		return ResponseEntity.status(BAD_REQUEST).body(validations);
	}

	// For @Valid
	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		LOG.info("Validation exception: {}", e.getMessage());

		Errors validations = new Errors(BAD_REQUEST.value(),
				e.getBindingResult().getAllErrors().stream().map(Error::new).collect(toList()));
		return ResponseEntity.status(BAD_REQUEST).body(validations);
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

	@Value
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

		public Error(ConstraintViolation<?> err) {
			this.field = err.getPropertyPath().toString();
			this.msg = err.getMessage();
		}

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
