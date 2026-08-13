CREATE TABLE inspecciones (
    id BIGSERIAL PRIMARY KEY,
    tema VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha_inspeccion DATE NOT NULL,
    hora_inspeccion TIME NOT NULL,
    observaciones VARCHAR(500),
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    creado_por VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inspecciones_responsables (
    inspeccion_id BIGINT NOT NULL,
    trabajador_id BIGINT NOT NULL,
    PRIMARY KEY (inspeccion_id, trabajador_id),
    CONSTRAINT fk_inspeccion_responsable_inspeccion
        FOREIGN KEY (inspeccion_id) REFERENCES inspecciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_inspeccion_responsable_trabajador
        FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE
);

INSERT INTO inspecciones (tema, tipo, fecha_inspeccion, hora_inspeccion, observaciones, estado, creado_por)
VALUES
    ('Inspección de EPP en planta', 'PLANEADA', CURRENT_DATE + INTERVAL '1 day', '09:00:00', 'Revisión de cascos, guantes y botas.', 'PENDIENTE', 'admin@sst.com'),
    ('Inspección de extintores', 'RUTINARIA', CURRENT_DATE + INTERVAL '2 days', '10:30:00', 'Verificar estado de extintores y señalización.', 'REALIZADA', 'admin@sst.com'),
    ('Inspección de orden y limpieza', 'ESPECIAL', CURRENT_DATE + INTERVAL '3 days', '15:00:00', 'Control de áreas de trabajo y almacenamiento seguro.', 'RETRASADA', 'admin@sst.com')
ON CONFLICT DO NOTHING;

INSERT INTO inspecciones_responsables (inspeccion_id, trabajador_id)
SELECT 1, id FROM trabajadores LIMIT 3
ON CONFLICT DO NOTHING;

INSERT INTO inspecciones_responsables (inspeccion_id, trabajador_id)
SELECT 2, id FROM trabajadores LIMIT 2
ON CONFLICT DO NOTHING;

INSERT INTO inspecciones_responsables (inspeccion_id, trabajador_id)
SELECT 3, id FROM trabajadores LIMIT 4
ON CONFLICT DO NOTHING;
