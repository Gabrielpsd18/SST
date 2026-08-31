ALTER TABLE documentos_personales
    ADD COLUMN solicitud_id BIGINT NULL;

CREATE TABLE documentos_solicitudes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    message TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    requested_by VARCHAR(100) NOT NULL,
    validated_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    validated_at TIMESTAMP NULL,
    CONSTRAINT fk_documentos_solicitudes_usuario FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

ALTER TABLE documentos_personales
    ADD CONSTRAINT fk_documentos_personales_solicitud
    FOREIGN KEY (solicitud_id) REFERENCES documentos_solicitudes(id) ON DELETE SET NULL;

CREATE INDEX idx_documentos_solicitudes_user_status ON documentos_solicitudes (user_id, status);
CREATE INDEX idx_documentos_personales_solicitud ON documentos_personales (solicitud_id);
