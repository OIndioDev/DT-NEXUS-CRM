package com.dtnexus.crm.repository;

import com.dtnexus.crm.model.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
    List<ServiceCatalog> findByTenantAndActiveTrueOrderByServiceNameAsc(String tenant);
}
