CREATE TABLE import_errors (
    id BIGSERIAL PRIMARY KEY,
    import_batch_id BIGINT,
    dni VARCHAR(50),
    trabajador VARCHAR(200),
    telefono VARCHAR(50),
    sede VARCHAR(200),
    cargo VARCHAR(200),
    error_message VARCHAR(1000),
    created_at TIMESTAMP DEFAULT now(),
    CONSTRAINT fk_import_errors_batch
        FOREIGN KEY (import_batch_id)
        REFERENCES import_batch(id)
        ON DELETE CASCADE
);
