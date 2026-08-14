# 🔴 REPORTE DE ERRORES EN MIGRACIONES SQL

## Fecha de Análisis
2026-08-14

---

## ❌ ERROR CRÍTICO #1 - V5__create_capacitaciones_tables.sql

### Problema
**Falta columna en la tabla `capacitaciones`**

La tabla `capacitaciones` define una restricción de clave foránea para `capacitador_id`:
```sql
CONSTRAINT fk_capacitacion_capacitador FOREIGN KEY (capacitador_id) REFERENCES capacitadores(id)
```

**PERO** la columna `capacitador_id` NO está definida en la tabla.

### Ubicación
Línea en el CREATE TABLE capacitaciones

### Impacto
- ❌ La migración fallará al ejecutarse
- ❌ Error: "column "capacitador_id" does not exist"
- ❌ La tabla NO se creará correctamente

### Solución
**Agregar la columna faltante** antes del CONSTRAINT:

```sql
CREATE TABLE capacitaciones (
    id BIGSERIAL PRIMARY KEY,
    tema VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha_programada TIMESTAMP NOT NULL,
    duracion_horas NUMERIC(4,2) NOT NULL,
    creado_por VARCHAR(100),
    capacitador_id BIGINT NOT NULL,  -- ← AGREGAR ESTA LÍNEA
    estado VARCHAR(30) DEFAULT 'PROGRAMADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_capacitacion_capacitador FOREIGN KEY (capacitador_id) REFERENCES capacitadores(id)
);
```

**Alternativa:** Si no es obligatorio que cada capacitación tenga un capacitador, usar relación N:N:
- Usar solo la tabla `capacitacion_capacitadores` (que ya existe)
- Eliminar el CONSTRAINT de `capacitador_id` en la tabla `capacitaciones`

---

## ⚠️ OBSERVACIÓN #2 - Orden de Migraciones

### Tabla `capacitacion_evaluaciones` y `capacitacion_videos`
Se crean **ANTES** que la tabla `capacitaciones` a la que hacen referencia.

Líneas en V5:
1. Se crea `capacitacion_evaluaciones` con FK a `capacitaciones`
2. Se crea `capacitacion_videos` con FK a `capacitaciones`
3. Se crea `capacitaciones` (tabla referenciada)

### Solución
Reordenar: Crear primero `capacitaciones`, luego las tablas dependientes.

---

## ✅ VERIFICACIONES EXITOSAS

### V1__init_schema.sql
- ✓ FK `rol_id` en tabla `usuarios` referencia correctamente `roles(id)`
- ✓ Sintaxis correcta

### V2__create_trabajadores_tables.sql
- ✓ FK `sede_id`, `cargo_id`, `usuario_id` correctas
- ✓ Todas las columnas están definidas
- ✓ Sintaxis correcta

### V8__create_inspecciones_tables.sql
- ✓ FK correctamente definidas
- ✓ Tabla relacional `inspecciones_responsables` correcta
- ✓ Sintaxis correcta

---

## 📋 RESUMEN DE CAMBIOS REQUERIDOS

| Migración | Tipo Error | Prioridad | Acción |
|-----------|-----------|-----------|--------|
| V5 | Columna faltante | 🔴 CRÍTICA | Agregar `capacitador_id` a tabla `capacitaciones` |
| V5 | Orden de creación | 🟠 ALTA | Reordenar creación de tablas dependientes |

