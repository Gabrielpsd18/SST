# Subsidios y Reparos

## Tipo
Módulo de negocio

## Objetivo

Administrar el proceso de recuperación de subsidios de EsSalud derivados de descansos médicos de los trabajadores.

---

## Entidad principal

Expediente

Información general

- Trabajador
- DNI
- Contingencia
- Estado
- Número de Expediente
- Número CITT (cuando exista)

---

## Flujo del proceso

### Fase 1 - Descanso Médico

Información registrada

- Responsable
- Trabajador
- Contingencia
- Fecha inicio
- Fecha fin

---

### Fase 2 - Trámite CITT

Información registrada

- ¿Existe CITT?
- Fecha de solicitud
- Número NIT (si aplica)
- Fecha de generación
- Número CITT

Documentos

- Indicaciones médicas
- Reporte operatorio
- Epicrisis
- Descanso médico

---

### Fase 3 - Reembolso EsSalud

Información registrada

- Fecha de presentación
- Número NIT
- Observaciones

Documentos

- CITT
- DNI
- Formulario 1040

---

### Fase 4 - Resolución

Información registrada

- Días subsidiados
- Valor diario
- Subsidio en planilla
- Subsidio mensual
- Monto recuperado
- Pendiente de recuperación
- Diferencia perdida
- Número de expediente EsSalud
- Respuesta EsSalud

---

## Relaciones

- Trabajador
- Descanso Médico
- Documentos
- Expediente

---

## Preguntas pendientes

- ¿Qué significa exactamente "Reparos"?
- ¿Qué representa el contador de días restantes? Es el plazo que tiene para completar una fase en conciso
- ¿El sistema calcula automáticamente los montos o solo los registra? Se registra creo
- ¿Puede existir más de un CITT por expediente? 