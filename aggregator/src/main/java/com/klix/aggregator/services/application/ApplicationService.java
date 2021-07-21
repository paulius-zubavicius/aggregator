package com.klix.aggregator.services.application;

public interface ApplicationService {

	Application create(UserRequest req);

	Application findById(Long id);

}
