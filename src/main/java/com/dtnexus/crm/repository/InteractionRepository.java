package com.dtnexus.crm.repository;

import com.dtnexus.crm.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByTenantIdAndLeadId(String tenantId, Long leadId);
}