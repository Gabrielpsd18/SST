-- 1. Tabla de Capacitadores / Instructores (Internos y Externos)
CREATE TABLE capacitadores (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100),
    empresa VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(128),
    especialidad VARCHAR(100),
    estado BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Sesiones de Capacitación
CREATE TABLE capacitaciones (
    id BIGSERIAL PRIMARY KEY,
    tema VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- CHARLA_5_MINUTOS, INDUCCION, CAPACITACION
    fecha_programada TIMESTAMP NOT NULL,
    duracion_horas NUMERIC(4,2) NOT NULL,
    capacitador_id BIGINT NOT NULL,
    creado_por VARCHAR(100),
    estado VARCHAR(30) DEFAULT 'PROGRAMADO', -- PROGRAMADO, EN_CURSO, COMPLETADO, CANCELADO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_capacitacion_capacitador FOREIGN KEY (capacitador_id) REFERENCES capacitadores(id)
);

-- 3. Tabla Relacional de Asignación de Trabajadores a Capacitación
CREATE TABLE capacitacion_trabajadores (
    capacitacion_id BIGINT NOT NULL,
    trabajador_id BIGINT NOT NULL,
    asistencia VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, ASISTIO, FALTO
    PRIMARY KEY (capacitacion_id, trabajador_id),
    CONSTRAINT fk_cap_trab_capacitacion FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_cap_trab_trabajador FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE
);

-- 4. Inserción de Datos Iniciales de Capacitadores de Prueba
INSERT INTO capacitadores (nombres, apellidos, empresa, telefono, correo, especialidad)
VALUES
('Ing. Roberto', 'Gómez', 'SST Consultores SAC', '987111222', 'rgomez@sstconsultores.com', 'Prevención de Riesgos Industriales'),
('Lic. Maria', 'Fernández', 'Seguridad Total Perú', '987333444', 'mfernandez@seguridadtotal.pe', 'Higiene y Salud Ocupacional'),
('Ing. Carlos', 'Ruiz', 'Interno SST', '987555666', 'cruiz@empresa.com', 'Primeros Auxilios y Ergonomía')
ON CONFLICT DO NOTHING;

-- 5. Inserción de Capacitaciones Iniciales de Prueba
INSERT INTO capacitaciones (tema, tipo, fecha_programada, duracion_horas, capacitador_id, creado_por, estado)
VALUES
('Charla de 5 Minutos: Identificación de Peligros en Planta', 'CHARLA_5_MINUTOS', CURRENT_TIMESTAMP + INTERVAL '1 day', 0.50, 1, 'admin@sst.com', 'PROGRAMADO'),
('Inducción General de Seguridad para Nuevos Ingresos', 'INDUCCION', CURRENT_TIMESTAMP + INTERVAL '3 days', 4.00, 2, 'admin@sst.com', 'PROGRAMADO'),
('Capacitación Teórico-Práctica: Ergonomía y Pausas Activas', 'CAPACITACION', CURRENT_TIMESTAMP + INTERVAL '7 days', 2.00, 3, 'admin@sst.com', 'PROGRAMADO')
ON CONFLICT DO NOTHING;

-- 6. Asignación inicial de trabajadores a la primera capacitación
INSERT INTO capacitacion_trabajadores (capacitacion_id, trabajador_id, asistencia)
SELECT 1, id, 'PENDIENTE' FROM trabajadores LIMIT 5
ON CONFLICT DO NOTHING;
