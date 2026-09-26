CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(120) NOT NULL,
    tenant VARCHAR(80) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL DEFAULT 'ROLE_GERENTE',
    trial_start_date DATE NOT NULL,
    account_status VARCHAR(30) NOT NULL DEFAULT 'TRIAL_ATIVO',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username_tenant (username, tenant),
    CONSTRAINT chk_users_account_status CHECK (account_status IN ('TRIAL_ATIVO', 'EXPIRADO', 'ASSINANTE_PAGO'))
) ENGINE=InnoDB;
