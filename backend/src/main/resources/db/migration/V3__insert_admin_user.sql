-- 1. Insertar Catálogos de Trabajadores
INSERT INTO sedes (nombre) VALUES ('HUSARES'), ('MUNDO MAGICO'), ('LARCO'), ('QUINTANAS'), ('HUAYNA CAPAC'), ('LAZARTE'), ('MOCHE'), ('COVICORTI'), ('HUANCHACO'), ('BUENOS AIRES'), ('CHICLAYO'), ('CASA DEL BEBE'), ('HELADERIA YAMBOLY'), ('HOTEL HUAKARUTE') ON CONFLICT DO NOTHING;
INSERT INTO cargos (nombre) VALUES ('Cajero'), ('Call Center'), ('Azafata'), ('Mozo'), ('Analista de SST'), ('Vendedor de Postres'), ('Vajillero'), ('Hornero'), ('Seguridad'), ('Encargado de Reservas'), ('Administrador'), ('Vigilante'), ('Ensaladero'), ('Vajillera'), ('Parrillero'), ('Mantenimiento'), ('Asistente de Contabilidad'), ('Jefe de Tienda'), ('Jefe de Logística'), ('Gerente General'), ('Limpieza'), ('Asistente de Auditoría'), ('Corredor'), ('Motorizado'), ('Asistente de Recursos Humanos'), ('Bartender'), ('Cocinero'), ('Descansero'), ('Barman'), ('Chofer'), ('Ayudante de Horno'), ('Jefe de Auditoría') ON CONFLICT DO NOTHING;
--HUSARES - MUNDO MAGICO - LARCO - QUINTANAS - HUAYNA CAPAC - LAZARTE - MOCHE - COVICORTI - HUANCHACO - BUENOS AIRES - CHICLAYO - CASA DEL BEBE - HELADERIA YAMBOLY -  HOTEL HUAKARUTE
--Cajero, Call Center, Azafata, Mozo, Analista de Ssoma, Vendedor de Postres, Vajillero, Hornero, Seguridad, Encargado de Reservas, Administrador, Vigilante, Ensaladero, Vajillera, Parrillero, Mantenimiento, Asistente de Contabilidad, Jefe de Tienda, Jefe de Logística, Gerente General, Limpieza, Asistente de Auditoria, Corredor, Motorizado, Asistente de Recursos Humanos, Bartender, Cocinero, Descansero, Barman, Chofer, Ayudante de Horno, Jefe de Auditoría.

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
    sede_id, cargo_id, usuario_id
) VALUES (
    'DNI', '74839201', 'Gabriel', 'Salgado Durán', 
    '987654321', 'gabrielsalgadoduran@gmail.com', 'PERMANENTE', 
    1, 2, 1
);