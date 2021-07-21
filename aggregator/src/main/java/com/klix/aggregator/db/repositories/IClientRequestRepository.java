package com.klix.aggregator.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klix.aggregator.db.model.ClientRequestEntity;

public interface IClientRequestRepository extends JpaRepository<ClientRequestEntity, Long> {

}
