-- Consolidar nombres y apellidos en nombre_completo
ALTER TABLE trabajadores ADD COLUMN IF NOT EXISTS nombre_completo VARCHAR(200);

UPDATE trabajadores
SET nombre_completo = TRIM(nombres || ' ' || apellidos)
WHERE nombre_completo IS NULL;

ALTER TABLE trabajadores ALTER COLUMN nombre_completo SET NOT NULL;

ALTER TABLE trabajadores DROP COLUMN IF EXISTS nombres;
ALTER TABLE trabajadores DROP COLUMN IF EXISTS apellidos;

-- Eliminar relación con áreas (si existía en instalaciones anteriores)
ALTER TABLE trabajadores DROP CONSTRAINT IF EXISTS fk_trabajador_area;
ALTER TABLE trabajadores DROP COLUMN IF EXISTS area_id;

DROP TABLE IF EXISTS areas;
