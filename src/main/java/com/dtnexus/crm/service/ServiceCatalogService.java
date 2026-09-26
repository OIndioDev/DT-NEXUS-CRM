package com.dtnexus.crm.service;

import com.dtnexus.crm.model.ServiceCatalog;
import com.dtnexus.crm.repository.ServiceCatalogRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServiceCatalogService {

    private final ServiceCatalogRepository repository;

    public ServiceCatalogService(ServiceCatalogRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = "serviceCatalog", key = "#tenantId")
    public List<ServiceCatalog> findActive(String tenant) {
        return repository.findByTenantAndActiveTrueOrderByServiceNameAsc(tenant);
    }

    @Transactional
    @CacheEvict(cacheNames = "serviceCatalog", key = "#request.tenant()")
    public ServiceCatalog create(com.dtnexus.crm.web.ServiceCatalogRequest request) {
        ServiceCatalog item = new ServiceCatalog();
        apply(item, request);
        return repository.save(item);
    }

    private void apply(ServiceCatalog item, com.dtnexus.crm.web.ServiceCatalogRequest request) {
        item.setTenant(request.tenant());
        item.setServiceName(request.serviceName());
        item.setCategory(request.category());
        item.setCostPrice(request.costPrice());
        item.setSalePrice(request.salePrice());
        item.setActive(request.active() == null || request.active());
    }
}
