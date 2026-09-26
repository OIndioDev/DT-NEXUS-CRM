package com.dtnexus.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId = "default"; // Isolamento Multi-Tenant seguro entre oficinas

    private String name;

    // Mantido para compatibilidade com cadastros antigos; usado como empresa/frota quando aplicável.
    private String company;

    @Column(name = "cargo_empresa")
    // Campo legado; pode armazenar o responsável pelo atendimento quando necessário.
    private String jobTitle;

    @Column(name = "contact_value")
    private Double value;

    @Column(name = "service_value")
    private Double serviceValue;

    @Column(name = "parts_value")
    private Double partsValue;

    private String statusColumn;
    private String phone;

    // Campo legado mantido para não perder dados existentes durante a transição do nicho.
    private String companySize;

    @Column(name = "email")
    private String email; // Novo: e-mail de contato do lead, usado na Proposta Comercial

    @Column(name = "customer_type")
    private String customerType; // Cliente Particular / Frota / Empresa / Oficina Parceira

    @Column(name = "vehicle_model")
    private String vehicleModel; // Ex: Honda Civic, Hilux, CG 160

    @Column(name = "vehicle_plate")
    private String vehiclePlate; // Placa do veículo, ajuda a puxar histórico quando o carro volta

    @Column(name = "vehicle_chassis")
    private String vehicleChassis;

    @Column(name = "vehicle_km")
    private Integer vehicleKm;

    @Column(name = "inspection_external", columnDefinition = "TEXT")
    private String vehicleInspectionExternal;

    @Column(name = "inspection_internal")
    private String vehicleInspectionInternal;

    @Column(name = "fluid_status")
    private String vehicleFluidStatus;

    @Column(name = "fuel_level")
    private String vehicleFuelLevel;

    @Column(name = "signature_accepted")
    private Boolean vehicleSignatureAccepted;

    @Column(name = "service_interest")
    private String serviceInterest; // Alinhamento, Balanceamento, Troca de Pneu...

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now(); // Campo de auditoria para o n8n e relatórios

    public Contact() {}

    public Contact(String name, String company, String jobTitle, Double value, String statusColumn, String phone, String companySize) {
        this(name, company, jobTitle, value, null, null, statusColumn, phone, companySize);
    }

    public Contact(String name, String company, String jobTitle, Double value, Double serviceValue, Double partsValue,
                   String statusColumn, String phone, String companySize) {
        this(name, company, jobTitle, value, serviceValue, partsValue, statusColumn, phone, companySize,
            null, null, null, null, null);
    }

    public Contact(String name, String company, String jobTitle, Double value, Double serviceValue, Double partsValue,
                   String statusColumn, String phone, String companySize, String vehicleInspectionExternal,
                   String vehicleInspectionInternal, String vehicleFluidStatus, String vehicleFuelLevel,
                   Boolean vehicleSignatureAccepted) {
        this.name = name;
        this.company = company;
        this.jobTitle = jobTitle;
        this.value = value;
        this.serviceValue = serviceValue;
        this.partsValue = partsValue;
        this.statusColumn = statusColumn;
        this.phone = phone;
        this.companySize = companySize;
        this.vehicleInspectionExternal = vehicleInspectionExternal;
        this.vehicleInspectionInternal = vehicleInspectionInternal;
        this.vehicleFluidStatus = vehicleFluidStatus;
        this.vehicleFuelLevel = vehicleFuelLevel;
        this.vehicleSignatureAccepted = vehicleSignatureAccepted;
        this.tenantId = "default";
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Double getServiceValue() { return serviceValue; }
    public void setServiceValue(Double serviceValue) { this.serviceValue = serviceValue; }

    public Double getPartsValue() { return partsValue; }
    public void setPartsValue(Double partsValue) { this.partsValue = partsValue; }

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

    public String getVehicleChassis() { return vehicleChassis; }
    public void setVehicleChassis(String vehicleChassis) { this.vehicleChassis = vehicleChassis; }

    public Integer getVehicleKm() { return vehicleKm; }
    public void setVehicleKm(Integer vehicleKm) { this.vehicleKm = vehicleKm; }

    public String getVehicleInspectionExternal() { return vehicleInspectionExternal; }
    public void setVehicleInspectionExternal(String vehicleInspectionExternal) { this.vehicleInspectionExternal = vehicleInspectionExternal; }

    public String getVehicleInspectionInternal() { return vehicleInspectionInternal; }
    public void setVehicleInspectionInternal(String vehicleInspectionInternal) { this.vehicleInspectionInternal = vehicleInspectionInternal; }

    public String getVehicleFluidStatus() { return vehicleFluidStatus; }
    public void setVehicleFluidStatus(String vehicleFluidStatus) { this.vehicleFluidStatus = vehicleFluidStatus; }

    public String getVehicleFuelLevel() { return vehicleFuelLevel; }
    public void setVehicleFuelLevel(String vehicleFuelLevel) { this.vehicleFuelLevel = vehicleFuelLevel; }

    public Boolean getVehicleSignatureAccepted() { return vehicleSignatureAccepted; }
    public void setVehicleSignatureAccepted(Boolean vehicleSignatureAccepted) { this.vehicleSignatureAccepted = vehicleSignatureAccepted; }

    public String getServiceInterest() { return serviceInterest; }
    public void setServiceInterest(String serviceInterest) { this.serviceInterest = serviceInterest; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
