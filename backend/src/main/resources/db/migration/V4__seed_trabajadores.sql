-- 1. Asegurar la existencia de los roles de la aplicación
INSERT INTO roles (nombre) VALUES ('ADMINISTRADOR'), ('SUPERVISOR'), ('TRABAJADOR') ON CONFLICT (nombre) DO NOTHING;

-- 2. Insertar Usuario Supervisor de Prueba (password: 12345 bctype)
INSERT INTO usuarios (email, password, activo, rol_id, created_at, updated_at)
VALUES (
    'supervisor@sst.com',
    '$2a$10$e88yR.3mK0L.9K7o/1.z2.X.X1Z9g/7S.x0N5k1Z2g/7S.x0N5k1Z',
    TRUE,
    (SELECT id FROM roles WHERE nombre = 'SUPERVISOR'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- 3. Insertar Trabajadores de Prueba (Seeders representativos)
INSERT INTO trabajadores (tipo_documento, numero_documento, nombre_completo, telefono, correo_notificaciones, tipo_contrato, sede_id, cargo_id, usuario_id, estado)
VALUES
('DNI', '45891234', 'Carlos Alberto Mendoza Quispe', '984512301', 'cmendoza@empresa.com', 'PERMANENTE', 1, 1, NULL, 'ACTIVO'),
('DNI', '71234567', 'María Elena Torres Rojas', '971234567', 'mtorres@empresa.com', 'PERMANENTE', 1, 2, (SELECT id FROM usuarios WHERE email = 'supervisor@sst.com'), 'ACTIVO'),
('DNI', '09876543', 'Jorge Luis Pérez Gómez', '909876543', 'jperez@empresa.com', 'TEMPORAL', 2, 3, NULL, 'ACTIVO'),
('CE', '001234567', 'Ana Sofía González Silva', '912345678', 'agonzalez@empresa.com', 'PERMANENTE', 2, 2, NULL, 'ACTIVO'),
('DNI', '44332211', 'Roberto Carlos Vargas Castro', '944332211', 'rvargas@empresa.com', 'PRACTICANTE', 3, 4, NULL, 'ACTIVO'),
('DNI', '12344321', 'Lucía Fernanda Ramírez Morales', '912344321', 'lramirez@empresa.com', 'PERMANENTE', 3, 3, NULL, 'ACTIVO'),
('PASAPORTE', 'P987654', 'David Alejandro Martínez Díaz', '998765432', 'dmartinez@empresa.com', 'TEMPORAL', 1, 2, NULL, 'INACTIVO'),
('DNI', '78901234', 'Patricia Vanessa Flores Benítez', '978901234', 'pflores@empresa.com', 'PERMANENTE', 2, 2, NULL, '
ACTIVO'),
('DNI', '56789012', 'Luis Alberto Castillo Rojas', '956789012', 'lcastillo@empresa.com', 'TEMPORAL', 3, 1, NULL, 'ACTIVO'),
('DNI', '32109876', 'Rosa Isabel Guerrero Ríos', '932109876', 'rguerrero@empresa.com', 'PERMANENTE', 1, 3, NULL, 'ACTIVO'),
('DNI', '89012345', 'Fernando José Sánchez Azaña', '989012345', 'fsanchez@empresa.com', 'RESPONSABLE', 2, 2, NULL, 'ACTIVO'),
('CE', '009876543', 'Carmen Rosa Navarro Medina', '900987654', 'cnavarro@empresa.com', 'PERMANENTE', 3, 2, NULL, 'INACTIVO')
ON CONFLICT (numero_documento) DO NOTHING;
