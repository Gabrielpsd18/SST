-- Tablas Catálogo
CREATE TABLE sedes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    estado BOOLEAN DEFAULT TRUE
);


CREATE TABLE cargos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    estado BOOLEAN DEFAULT TRUE
);

-- Tabla Principal de Trabajadores
CREATE TABLE trabajadores (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL, -- DNI, CE, PASAPORTE
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombre_completo VARCHAR(200) NOT NULL,
    telefono VARCHAR(20),
    correo_notificaciones VARCHAR(128),
    tipo_contrato VARCHAR(30) NOT NULL, -- PERMANENTE, TEMPORAL, PRACTICANTE, RESPONSABLE
    
    -- Relaciones con Tablas Maestras
    sede_id BIGINT NOT NULL,
    cargo_id BIGINT NOT NULL,
    
    -- Relación Opcional con la tabla de Usuarios de Auth (Si el trabajador tiene acceso al sistema)
    usuario_id BIGINT UNIQUE,
    
    estado VARCHAR(20) DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trabajador_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_trabajador_cargo FOREIGN KEY (cargo_id) REFERENCES cargos(id),
    CONSTRAINT fk_trabajador_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
