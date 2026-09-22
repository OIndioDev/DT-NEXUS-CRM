package com.dtnexus.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;

    @Column(name = "cargo_empresa")
    private String jobTitle; // Ajustado para 'jobTitle' para coincidir com o Thymeleaf, mantendo a coluna 'cargo_empresa' no banco

    @Column(name = "contact_value")
    private Double value;
    private String statusColumn;
    private String phone;
    private String companySize; // Adicionado para resolver o erro do Thymeleaf

    @Column(name = "email")
    private String email; // Novo: e-mail de contato do lead, usado na Proposta Comercial

    @Column(name = "customer_type")
    private String customerType; // Consumidor Final / Oficina Parceira / Frota

    @Column(name = "vehicle_model")
    private String vehicleModel; // Ex: Honda Civic, Hilux, CG 160

    @Column(name = "vehicle_plate")
    private String vehiclePlate; // Placa do veículo, ajuda a puxar histórico quando o carro volta

    @Column(name = "service_interest")
    private String serviceInterest; // Alinhamento, Balanceamento, Troca de Pneu, Troca de Óleo, Freios, Suspensão, Bateria, Correia Dentada, Ar-Condicionado, Diagnóstico Elétrico/Injeção, Revisão Completa, Peça Específica...

    public Contact() {}

    public Contact(String name, String company, String jobTitle, Double value, String statusColumn, String phone, String companySize) {
        this.name = name;
        this.company = company;
        this.jobTitle = jobTitle;
        this.value = value;
        this.statusColumn = statusColumn;
        this.phone = phone;
        this.companySize = companySize;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getStatusColumn() { return statusColumn; }
    public void setStatusColumn(String statusColumn) { this.statusColumn = statusColumn; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompanySize() { return companySize; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }

    public String getServiceInterest() { return serviceInterest; }
    public void setServiceInterest(String serviceInterest) { this.serviceInterest = serviceInterest; }
}