##### Exámenes Médicos Ocupacionales (EMO)

# Tipo
Módulo de negocio

# Objetivo
Administrar los Exámenes Médicos Ocupacionales realizados a los trabajadores y mantener su historial médico ocupacional.

# Información principal

# Trabajador
- Trabajador
- Estado del EMO
- Fecha del examen

# Datos del EMO
- Tipo de EMO
    - Pre-ocupacional
    - Periódico
    - Retiro
    - Reubicación
    - Subcontratista
    - Otros (pendiente de validar)

- Fecha del EMO
- Hora programada
- Protocolo
- Centro médico
- Proyecto
- Observaciones adicionales
- Recomendaciones generales
- Sede RRHH
- Tipo Examen
- Posterior al examen medico, hacer levantamiento que es datos del trabajador, resultados mas observaciones
- Vigilancia Medica, diagnostico, especialidad, plazo de levantamiento, estado

# Evidencias

- Documento PDF del EMO
- Archivos adicionales (si aplica)

# Permisos

Administrador
- Gestiona todos los EMOs.
- Puede registrar, editar y actualizar recomendaciones.

Supervisor
- Consulta únicamente los trabajadores de su sede o área.

Trabajador
- Consulta únicamente sus propios EMOs y descarga sus documentos.

# Relaciones

- Trabajador
- Centro Médico
- Proyecto
- Documentos

# Preguntas pendientes

- ¿Qué significa exactamente "Protocolo"?
- ¿Puede existir más de un protocolo por EMO?
- ¿Quién carga el PDF?
- ¿Se registra la aptitud médica (Apto / No apto / Apto con restricciones)?


#### Vigilancia Médica

# Tipo
Módulo de negocio (Seguimiento)

# Objetivo
Realizar el seguimiento a trabajadores cuyos Exámenes Médicos Ocupacionales requieren control o atención especial.

---

# Origen

La información proviene de los Exámenes Médicos Ocupacionales (EMO).

Un trabajador ingresa a Vigilancia Médica únicamente cuando el resultado del EMO requiere seguimiento.

---

# Información mostrada

- Trabajador
- EMO relacionado
- Fecha del examen
- Diagnóstico o resultado
- Nivel de prioridad
- Estado del seguimiento

---

# Seguimiento

Permite registrar:

- Observaciones
- Acciones realizadas
- Fecha de seguimiento
- Responsable
- Estado

---

# Relaciones

- Trabajador
- EMO
- Seguimientos
- Documentos (si existen)

---

# Preguntas pendientes

- ¿Quién decide que un EMO pasa a Vigilancia Médica?
- ¿Qué estados existen? (Pendiente, En seguimiento, Cerrado...)
- ¿Puede tener varios seguimientos?

