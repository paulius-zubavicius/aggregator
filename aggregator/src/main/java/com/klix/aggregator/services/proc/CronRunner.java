package com.klix.aggregator.services.proc;

import static com.klix.aggregator.db.model.RequestStatus.CREATED;
import static com.klix.aggregator.db.model.RequestStatus.DRAFT;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.klix.aggregator.db.model.ProcessStatus;
import com.klix.aggregator.db.model.RequestStatus;
import com.klix.aggregator.services.storage.StorageService;

@Component
public class CronRunner {

	private static final List<RequestStatus> WAITING_FOR_PROC = Arrays.asList(CREATED, DRAFT);

	@Autowired
	private StorageService storageService;

	@Autowired
	private ProcessingService service;

	@Scheduled(cron = "${banks.cron}")
	void processRequests() {
		storageService.findByStatuses(ProcessStatus.READY, WAITING_FOR_PROC).parallelStream().forEach(service::process);
	}
}
