CREATE TABLE IF NOT EXISTS service_catalog (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_name VARCHAR(120) NOT NULL,
    category VARCHAR(80) NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    tenant VARCHAR(80) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_service_catalog_tenant_active (tenant, active),
    INDEX idx_service_catalog_tenant_name (tenant, service_name)
) ENGINE=InnoDB;
