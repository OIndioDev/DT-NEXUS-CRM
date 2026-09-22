package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Lead;
import com.dtnexus.crm.repository.LeadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;

    public LeadController(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @GetMapping
    public List<Lead> getAllLeads(@RequestParam String tenantId) {
        return leadRepository.findByTenantId(tenantId);
    }

    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadRepository.save(lead);
    }
}