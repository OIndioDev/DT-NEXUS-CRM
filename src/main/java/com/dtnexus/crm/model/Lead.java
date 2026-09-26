package com.dtnexus.crm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    @NotBlank(message = "tenantId é obrigatório")
    private String tenantId;

    @Column(nullable = false)
    @NotBlank(message = "name é obrigatório")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    private String email;

    private String phone;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "deal_value")
    private Double dealValue;

    @Column(name = "status_column", nullable = false)
    private String statusColumn = "BACKLOG";

    @Column(name = "sentiment_score")
    private String sentimentScore;

    @Column(name = "next_best_action", columnDefinition = "TEXT")
    private String nextBestAction;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Construtor Padrão Necessário para o Hibernate
    public Lead() {
    }

    // Construtor Completo
    public Lead(Long id, String tenantId, String name, String email, String phone, String companyName, 
                Double dealValue, String statusColumn, String sentimentScore, String nextBestAction, 
                String aiSummary, LocalDateTime createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.dealValue = dealValue;
        this.statusColumn = statusColumn;
        this.sentimentScore = sentimentScore;
        this.nextBestAction = nextBestAction;
        this.aiSummary = aiSummary;
        this.createdAt = createdAt;
    }

    // GETTERS E SETTERS EXPLÍCITOS (Garante compatibilidade total com o VS Code e LeadController)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getDealValue() {
        return dealValue;
    }

    public void setDealValue(Double dealValue) {
        this.dealValue = dealValue;
    }

    public String getStatusColumn() {
        return statusColumn;
    }

    public void setStatusColumn(String statusColumn) {
        this.statusColumn = statusColumn;
    }

    public String getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(String sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getNextBestAction() {
        return nextBestAction;
    }

    public void setNextBestAction(String nextBestAction) {
        this.nextBestAction = nextBestAction;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
