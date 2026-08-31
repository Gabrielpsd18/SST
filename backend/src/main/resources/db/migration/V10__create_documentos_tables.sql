CREATE TABLE documentos_generales (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    description TEXT,
    file_path VARCHAR(500) NOT NULL,
    version VARCHAR(50) NOT NULL DEFAULT '1.0',
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE documentos_personales (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    issue_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documentos_personales_usuario FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
