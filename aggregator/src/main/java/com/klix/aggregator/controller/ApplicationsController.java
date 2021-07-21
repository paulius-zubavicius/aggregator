package com.klix.aggregator.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klix.aggregator.services.application.Application;
import com.klix.aggregator.services.application.ApplicationService;
import com.klix.aggregator.services.application.UserRequest;

@RestController
@RequestMapping
@Validated
public class ApplicationsController {

	@Autowired
	private ApplicationService applications;

	@GetMapping(produces = APPLICATION_JSON_VALUE, path = "/{id}")
	public Application getById(@PathVariable("id") @NotNull Long id) {
		return applications.findById(id);
	}

	@PostMapping(produces = APPLICATION_JSON_VALUE)
	public Application submit(@Valid @RequestBody UserRequest req) {
		return applications.create(req);
	}

}
