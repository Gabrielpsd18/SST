-- 1. Insertar Catálogos de Trabajadores
INSERT INTO sedes (nombre) VALUES ('Sede Principal'), ('Sede Norte'), ('Planta Industrial') ON CONFLICT DO NOTHING;
INSERT INTO areas (nombre) VALUES ('Operaciones'), ('Recursos Humanos'), ('Mantenimiento'), ('SST') ON CONFLICT DO NOTHING;
INSERT INTO cargos (nombre) VALUES ('Técnico de Campo'), ('Analista SST'), ('Supervisor de Planta'), ('Operario') ON CONFLICT DO NOTHING;

-- 2. Insertar Usuario Admin (Usando la FK rol_id que apunta a ADMINISTRADOR)
INSERT INTO usuarios (email, password, activo, rol_id, created_at, updated_at)
VALUES (
    'admin@sst.com',
    '$2a$10$e88yR.3mK0L.9K7o/1.z2.X.X1Z9g/7S.x0N5k1Z2g/7S.x0N5k1Z',
    TRUE,
    (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- 3. Insertar Trabajador vinculado al Usuario
ALTER TABLE trabajadores ADD COLUMN correo_notificaciones VARCHAR(128);

INSERT INTO trabajadores (tipo_documento, numero_documento, nombres, apellidos, telefono, tipo_contrato, sede_id, area_id, cargo_id, usuario_id, estado)
VALUES (
    'DNI',
    '00000000',
    'Gabriel',
    'Salgado',
    '999999999',
    'PERMANENTE',
    1,
    1,
    1,
    (SELECT id FROM usuarios WHERE email = 'admin@sst.com'),
    'ACTIVO'
)
ON CONFLICT (numero_documento) DO NOTHING;