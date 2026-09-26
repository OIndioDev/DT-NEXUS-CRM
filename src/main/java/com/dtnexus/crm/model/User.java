package com.dtnexus.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String username;

    @Column(nullable = false, length = 80)
    private String tenant;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String roles = "ROLE_GERENTE";

    @Column(name = "trial_start_date", nullable = false)
    private LocalDate trialStartDate;

    @Column(name = "account_status", nullable = false, length = 30)
    private String accountStatus = "TRIAL_ATIVO";

    @PrePersist
    void initializeTrial() {
        if (trialStartDate == null) trialStartDate = LocalDate.now();
        if (accountStatus == null || accountStatus.isBlank()) accountStatus = "TRIAL_ATIVO";
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public LocalDate getTrialStartDate() { return trialStartDate; }
    public void setTrialStartDate(LocalDate trialStartDate) { this.trialStartDate = trialStartDate; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
}
