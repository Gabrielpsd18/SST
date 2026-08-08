-- 1. Insertar Catálogos de Trabajadores
INSERT INTO sedes (nombre) VALUES ('Sede Principal'), ('Sede Norte'), ('Planta Industrial') ON CONFLICT DO NOTHING;
INSERT INTO areas (nombre) VALUES ('Operaciones'), ('Recursos Humanos'), ('Mantenimiento'), ('SST') ON CONFLICT DO NOTHING;
INSERT INTO cargos (nombre) VALUES ('Técnico de Campo'), ('Analista SST'), ('Supervisor de Planta'), ('Operario') ON CONFLICT DO NOTHING;

-- 2. Insertar Usuario Admin (Usando la FK rol_id que apunta a ADMINISTRADOR)
INSERT INTO usuarios (email, password, activo, rol_id, created_at, updated_at)
VALUES (
    'admin@sst.com',
    '$2a$12$uGrolbHdng6nc62nccr2QebqrHu20Zv9Knz3tqr7FTIyHP7rSUmwq',
    TRUE,
    (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;



-- 3. Creamos tu trabajador y lo vinculamos al usuario_id y a los catálogos
INSERT INTO trabajadores (
    tipo_documento, numero_documento, nombres, apellidos, 
    telefono, correo_notificaciones, tipo_contrato, 
    sede_id, area_id, cargo_id, usuario_id
) VALUES (
    'DNI', '74839201', 'Gabriel', 'Salgado Durán', 
    '987654321', 'gabrielsalgadoduran@gmail.com', 'PERMANENTE', 
    1, 4, 2, 1 -- Sede Principal (1), SST (4), Analista SST (2), usuario_id (1)
);