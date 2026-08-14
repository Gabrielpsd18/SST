-- Flyway migration: create import tables
CREATE TABLE import_batch (
  id BIGSERIAL PRIMARY KEY,
  filename VARCHAR(255),
  month INT NOT NULL,
  year INT NOT NULL,
  importer_id BIGINT,
  created_at TIMESTAMP DEFAULT now(),
  status VARCHAR(32) DEFAULT 'PENDING',
  summary_created INT DEFAULT 0,
  summary_reactivated INT DEFAULT 0,
  summary_deactivated INT DEFAULT 0,
  summary_updated INT DEFAULT 0,
  summary_errors INT DEFAULT 0,
  notes TEXT
);

CREATE TABLE import_row_issue (
  id BIGSERIAL PRIMARY KEY,
  import_batch_id BIGINT REFERENCES import_batch(id) ON DELETE CASCADE,
  row_number INT,
  raw_row_json TEXT,
  issue_type VARCHAR(64),
  issue_details TEXT,
  resolved BOOLEAN DEFAULT false,
  resolved_by BIGINT,
  resolved_at TIMESTAMP,
  action_taken VARCHAR(64),
  created_at TIMESTAMP DEFAULT now()
);




create table roles(
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(50)
);
CREATE TABLE usuarios(
    id BIGSERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(128)  NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT  TRUE,
    rol_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (rol_id)
        REFERENCES roles(id)
);
INSERT INTO roles(nombre, descripcion)
VALUES
    ('ADMINISTRADOR','Acceso total'),
    ('SUPERVISOR','Supervisa trabajadores'),
    ('TRABAJADOR','Usuario operativo');
