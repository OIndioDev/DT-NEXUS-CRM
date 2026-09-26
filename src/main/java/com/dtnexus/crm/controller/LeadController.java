package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Lead;
import com.dtnexus.crm.repository.LeadRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;

    public LeadController(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // Listar todos os leads filtrados por Tenant de forma segura
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads(@RequestParam String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Lead> leads = leadRepository.findByTenantId(tenantId);
        return ResponseEntity.ok(leads);
    }

    // Criar um novo lead validando a presença do Tenant do ecossistema
    @PostMapping
    public ResponseEntity<?> createLead(@Valid @RequestBody Lead lead) {
        if (lead.getTenantId() == null || lead.getTenantId().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O identificador de Tenant (tenantId) é obrigatório para registrar um Lead.");
        }
        Lead savedLead = leadRepository.save(lead);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLead);
    }
}
