package com.klix.aggregator.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klix.aggregator.db.model.BankOfferEntity;
import com.klix.aggregator.db.model.ProcessStatus;
import com.klix.aggregator.db.model.RequestStatus;

public interface IBankOfferRepository extends JpaRepository<BankOfferEntity, Long> {

	List<BankOfferEntity> findByStatusProcAndStatusIn(ProcessStatus procStat, List<RequestStatus> statuses);

}
