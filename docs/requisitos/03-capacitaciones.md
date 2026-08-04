#### Capacitaciones

# Tipo
Módulo de negocio

# Objetivo
Planificar, ejecutar y realizar el seguimiento de las capacitaciones impartidas a los trabajadores.

---

# Información General

- Tema
- Tipo de capacitación
- Sede
- Área
- Grupo de capacitación 
- Fecha de registro

---

# Programación

- Fecha de inicio
- Hora de inicio
- Fecha de fin
- Hora de fin
- Ubicación

---

# Responsables

- Capacitador
- Empresa / Razón Social del Capacitor
- Responsable de asistencia
- Responsable de certificación (Depende)


---

# Participantes

El sistema debe permitir asignar uno o varios trabajadores a una capacitación.

Debe registrar:

- Cantidad programada
- Cantidad asistente
- Cantidad pendiente

Cada trabajador tendrá un estado:

- Pendiente
- Asistió
- No asistió
- Justificado (pendiente de confirmar)

---

# Evidencias

Durante la capacitación podrán adjuntarse:

- Lista de asistencia
- Certificados

Los metadatos (fecha, usuario que registró, etc.) pueden generarse automáticamente.

---

# Relaciones

- Trabajadores
- Grupo
- Empresa
- Área
- Documentos
- Evidencias

---

# Permisos

Administrador
- Crear capacitación
- Editar
- Programar
- Registrar asistencia
- Subir evidencias
- Emitir certificados

Supervisor
- Registrar asistencia
- Consultar

Trabajador
- Ver sus capacitaciones
- Descargar certificado
- Consultar evidencias autorizadas

---

# Preguntas pendientes

- ¿Qué significa exactamente "Publica"? 
- ¿Un trabajador puede pertenecer a varios grupos? Si
- ¿Puede existir más de un capacitador? Si (Por eso se forma los grupos, puede ser de la empresa o externo)
- ¿Los certificados son generados por el sistema o se cargan en PDF? Lo elabora otro y deberia poder cargarse al sistema para que los demás lo descarguen con sus datos agregados

# Charla de 5 minutos
Solo es una variante de capacitacion, pero tiene la misma informacion
# Igualmente las pausas activas

