package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.ServiceCatalog;
import com.dtnexus.crm.service.ServiceCatalogService;
import com.dtnexus.crm.web.ServiceCatalogRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-catalog")
public class ServiceCatalogController {

    private final ServiceCatalogService service;

    public ServiceCatalogController(ServiceCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceCatalog> listActive(@RequestParam String tenant) {
        return service.findActive(tenant);
    }

    @PostMapping
    public ResponseEntity<ServiceCatalog> create(@Valid @RequestBody ServiceCatalogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
