package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Lead;
import com.dtnexus.crm.repository.LeadRepository;
import com.dtnexus.crm.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;
    private final AiService aiService;

    public LeadController(LeadRepository leadRepository, AiService aiService) {
        this.leadRepository = leadRepository;
        this.aiService = aiService;
    }

    @GetMapping
    public List<Lead> getAllLeads(@RequestParam String tenantId) {
        return leadRepository.findByTenantId(tenantId);
    }

    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadRepository.save(lead);
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<String> analyzeLead(@PathVariable Long id, @RequestBody String interactionsText) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead não encontrado"));
        
        String analysis = aiService.analyzeLeadSentiment(lead.getName(), interactionsText);
        lead.setSentimentScore(analysis);
        leadRepository.save(lead);
        
        return ResponseEntity.ok(analysis);
    }
}