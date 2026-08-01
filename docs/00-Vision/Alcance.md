## Alcance del Proyecto

El proyecto contempla el desarrollo de una plataforma compuesta por una aplicación web, una aplicación móvil y un backend centralizado, orientada a la administración de los procesos de Seguridad y Salud en el Trabajo.

Inicialmente el sistema abarcará los siguientes dominios funcionales:

# Gestión de Personas
    Trabajadores
    Contratistas
    Jerarquía organizacional
    Áreas
    Cargos
    Sedes
    Unidades
# Salud Ocupacional
    Exámenes Médicos Ocupacionales (EMO)
    Vigilancia Médica
    Subsidios y procesos relacionados con EsSalud
    Seguimiento médico
# Gestión de Seguridad
    Inspecciones
    Accidentes
    Incidentes
    Actos y Condiciones
    Hallazgos
    Acciones Correctivas
    Seguimiento de acciones
    Monitoreos
    Matriz IPERC
# Gestión de Capacitación
    Inducciones
    Certificados
    Capacitaciones
    Asistencia
    Evidencias
# Gestión de Equipos de Protección Personal (EPP)
    Catálogo de EPP
    Solicitudes
    Entregas
    Kardex por trabajador
# Gestión Documentaria
    Programa Anual SST
    SGSST
    Comité SST
    Difusión de documentos
    Control documental
# Comunicación
    Comunicados
    Contactos SST
    Publicaciones para trabajadores
# Reportes
    Indicadores de cumplimiento
    Accidentes e incidentes
    Capacitaciones
    Comité SST
    Reportes operativos
# Administración del Sistema
    Usuarios
    Roles y permisos
    Alertas
    Parámetros
    Auditoría
    Configuración general
# Alcance Tecnológico
El proyecto contempla además:
    Backend centralizado mediante API REST.
    Aplicación Web para administradores y supervisores.
    Aplicación móvil para trabajadores.
    Base de datos relacional PostgreSQL.
    Arquitectura Modular basada en Clean Architecture.
    Autenticación mediante JWT.
    Gestión de archivos y evidencias en almacenamiento centralizado.
    Despliegue en infraestructura Cloud.
    Control de versiones mediante Git y GitHub.

# Beneficios Esperados
La implementación del sistema permitirá:
    Centralizar la información SST de la organización.
    Reducir el uso de procesos manuales y documentación física.
    Mejorar la trazabilidad de las actividades y evidencias.
    Facilitar el seguimiento de obligaciones legales y normativas.
    Disponer de información actualizada para la toma de decisiones.
    Mejorar la comunicación entre trabajadores, supervisores y administradores.
    Contar con una plataforma escalable preparada para incorporar nuevos módulos en el futuro.

# Principios de Diseño del Proyecto
Desde el inicio del proyecto se adoptarán los siguientes principios:
    Arquitectura orientada al dominio: los módulos se organizarán según procesos de negocio, no según pantallas o formularios.
    Modularidad: cada dominio será independiente y desacoplado para facilitar su mantenimiento y evolución.
    Reutilización: componentes comunes como documentos, evidencias, notificaciones y autenticación serán compartidos entre módulos.
    Escalabilidad: la arquitectura permitirá incorporar nuevas funcionalidades sin afectar las existentes.
    Seguridad: la protección de la información será un aspecto transversal mediante autenticación, autorización, auditoría y buenas prácticas de desarrollo.
    Mantenibilidad: el código y la estructura del proyecto estarán organizados para facilitar su comprensión y evolución por parte de un único desarrollador o de un equipo en el futuro.