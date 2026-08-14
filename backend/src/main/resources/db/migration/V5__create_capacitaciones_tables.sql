-- 1. PRIMERO: Crear la tabla principal
CREATE TABLE capacitaciones (
    id BIGSERIAL PRIMARY KEY,
    tema VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha_programada TIMESTAMP NOT NULL,
    duracion_horas NUMERIC(4,2) NOT NULL,
    creado_por VARCHAR(100),
    estado VARCHAR(30) DEFAULT 'PROGRAMADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Eliminé la coma que tenías aquí
);

-- 2. LUEGO: Crear las tablas que dependen de 'capacitaciones'
-- 1.1 Tabla de Capacitadores
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

-- 1.2 Tabla de Links de Formularios
CREATE TABLE capacitacion_evaluaciones (
    id BIGSERIAL PRIMARY KEY,
    capacitacion_id BIGINT NOT NULL,
    link_formulario VARCHAR(500) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_capacitacion_evaluacion FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE
);

-- 1.3 Tabla de links de Videos
CREATE TABLE capacitacion_videos (
    id BIGSERIAL PRIMARY KEY,
    capacitacion_id BIGINT NOT NULL,
    link_video VARCHAR(500) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_capacitacion_video FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE
);

-- 3.1. Tabla Relacional de Trabajadores
CREATE TABLE capacitacion_trabajadores (
    capacitacion_id BIGINT NOT NULL,
    trabajador_id BIGINT NOT NULL,
    asistencia VARCHAR(20) DEFAULT 'PENDIENTE',
    PRIMARY KEY (capacitacion_id, trabajador_id),
    CONSTRAINT fk_cap_trab_capacitacion FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_cap_trab_trabajador FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE
);

-- 3.2. Tabla Relacional de Capacitadores
CREATE TABLE capacitacion_capacitadores (
    capacitacion_id BIGINT NOT NULL,
    capacitador_id BIGINT NOT NULL,
    PRIMARY KEY (capacitacion_id, capacitador_id),
    CONSTRAINT fk_cap_capacitacion FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_cap_capacitador FOREIGN KEY (capacitador_id) REFERENCES capacitadores(id) ON DELETE CASCADE
);

-- (Luego mantienes tus INSERTs exactamente igual...)
-- 4. Inserción de Datos Iniciales de Capacitadores de Prueba
INSERT INTO capacitadores (nombres, apellidos, empresa, telefono, correo, especialidad)
VALUES
('Ing. Roberto', 'Gómez', 'SST Consultores SAC', '987111222', 'rgomez@sstconsultores.com', 'Prevención de Riesgos Industriales'),
('Lic. Maria', 'Fernández', 'Seguridad Total Perú', '987333444', 'mfernandez@seguridadtotal.pe', 'Higiene y Salud Ocupacional'),
('Ing. Carlos', 'Ruiz', 'Interno SST', '987555666', 'cruiz@empresa.com', 'Primeros Auxilios y Ergonomía')
ON CONFLICT DO NOTHING;

-- 5. Inserción de Capacitaciones Iniciales de Prueba
INSERT INTO capacitaciones (tema, tipo, fecha_programada, duracion_horas, creado_por, estado)
VALUES
('Charla de 5 Minutos: Identificación de Peligros en Planta', 'CHARLA_5_MINUTOS', CURRENT_TIMESTAMP + INTERVAL '1 day', 0.50, 'admin@sst.com', 'PROGRAMADO'),
('Inducción General de Seguridad para Nuevos Ingresos', 'INDUCCION', CURRENT_TIMESTAMP + INTERVAL '3 days', 4.00, 'admin@sst.com', 'PROGRAMADO'),
('Capacitación Teórico-Práctica: Ergonomía y Pausas Activas', 'CAPACITACION', CURRENT_TIMESTAMP + INTERVAL '7 days', 2.00, 'admin@sst.com', 'PROGRAMADO')
ON CONFLICT DO NOTHING;

-- 6. Asignación inicial de trabajadores a la primera capacitación
INSERT INTO capacitacion_trabajadores (capacitacion_id, trabajador_id, asistencia)
SELECT 1, id, 'PENDIENTE' FROM trabajadores LIMIT 5
ON CONFLICT DO NOTHING;
-- 7. Asignación inicial de capacitadores a la primera capacitación
INSERT INTO capacitacion_capacitadores (capacitacion_id, capacitador_id)
SELECT 1, id FROM capacitadores LIMIT 2
ON CONFLICT DO NOTHING;

--8. Inserción de Links de Evaluación y Videos para la primera capacitación
INSERT INTO capacitacion_evaluaciones (capacitacion_id, link_formulario)
VALUES
(1, 'https://forms.gle/evaluacion-capacitacion-1')
ON CONFLICT DO NOTHING;
INSERT INTO capacitacion_videos (capacitacion_id, link_video)
VALUES
(1, 'https://www.youtube.com/?app=desktop&hl=es')
ON CONFLICT DO NOTHING;
