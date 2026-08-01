# Arquitectura General del Sistema SST

| Documento | Arquitectura General |
|------------|----------------------|
| Proyecto | Sistema de Seguridad y Salud en el Trabajo (SST) |
| Versión | 1.0 |
| Estado | En diseño |
| Arquitecto | Proyecto Personal |
| Última actualización | Agosto 2026 |

---

# 1. Propósito

Este documento describe la arquitectura general del Sistema de Seguridad y Salud en el Trabajo (SST), estableciendo las decisiones arquitectónicas fundamentales que servirán como base para el desarrollo del proyecto.

Su objetivo es definir la estructura del sistema antes de iniciar la implementación, garantizando un diseño mantenible, escalable y alineado con las necesidades del negocio.

---

# 2. Objetivos Arquitectónicos

La arquitectura del sistema busca cumplir los siguientes objetivos:

- Separar claramente las responsabilidades de cada componente.
- Organizar el backend por dominios del negocio.
- Facilitar el mantenimiento y evolución del sistema.
- Permitir el crecimiento del proyecto sin grandes refactorizaciones.
- Favorecer la reutilización de componentes.
- Mantener una arquitectura simple para un único desarrollador.
- Evitar el acoplamiento entre módulos.
- Centralizar toda la lógica del negocio en el Backend.

---

# 3. Tipo de Arquitectura

El sistema utilizará una arquitectura Cliente-Servidor basada en una API REST.


                 +----------------------+
                 |      Web App         |
                 +----------+-----------+
                            |
                            |
                 HTTP / HTTPS (REST)
                            |
                            ▼
                 +----------------------+
                 |     API Backend      |
                 |     ASP.NET Core     |
                 +----------+-----------+
                            |
                 Lógica del Negocio
                            |
                            ▼
                 +----------------------+
                 |     PostgreSQL       |
                 +----------------------+
                            |
                            ▼
                  Almacenamiento de Datos


---

# 4. Estilo Arquitectónico

El Backend seguirá un enfoque de:

- Monolito Modular
- Clean Architecture
- Arquitectura en Capas
- Domain Driven Design (DDD) como guía de modelado
- API REST
- Dependency Injection

No se utilizarán microservicios.

---

# 5. Arquitectura Tecnológica

| Componente | Tecnología |
|------------|------------|
| Backend | ASP.NET Core (.NET 8) |
| API | REST |
| Base de Datos | PostgreSQL |
| ORM | Entity Framework Core |
| Web | (Por definir) |
| Mobile | (Por definir) |
| Autenticación | JWT |
| Control de Acceso | Roles y Permisos |
| Control de Versiones | Git + GitHub |

---

# 6. Principios Arquitectónicos

Durante todo el proyecto deberán respetarse los siguientes principios.

## 6.1 Simplicidad

Siempre se elegirá la solución más simple que resuelva correctamente el problema.

No se implementarán patrones innecesarios.

---

## 6.2 Modularidad

Cada módulo representará un dominio o subdominio del negocio.

Los módulos deberán ser altamente cohesivos y débilmente acoplados.

---

## 6.3 Separación de Responsabilidades

Cada componente tendrá una única responsabilidad.

No se mezclarán:

- lógica de negocio
- acceso a datos
- presentación
- infraestructura

---

## 6.4 Reutilización

Los componentes comunes deberán reutilizarse.

Ejemplos:

- Archivos
- Documentos
- Evidencias
- Notificaciones
- Auditoría
- Alertas

---

## 6.5 Escalabilidad

La arquitectura permitirá incorporar nuevos módulos sin modificar los existentes.

---

## 6.6 Mantenibilidad

Las decisiones arquitectónicas deberán facilitar:

- pruebas
- mantenimiento
- evolución
- refactorización

---

# 7. Arquitectura Cliente-Servidor

El sistema estará compuesto por tres capas principales.

Cliente

├── Aplicación Web
└── Aplicación Mobile

            │

            ▼

API REST

            │

            ▼

Backend

            │

            ▼

PostgreSQL

Toda la lógica del negocio residirá exclusivamente en el Backend.

Los clientes únicamente consumirán la API.

---

# 8. Arquitectura del Backend

El Backend será un Monolito Modular organizado por dominios del negocio.

No se organizará por:

- pantallas
- formularios
- tipos de usuario
- reportes

La organización estará basada únicamente en conceptos del negocio.

Ejemplo:


Personas

Seguridad

Salud Ocupacional

Capacitaciones

Documentación

EPP

Comunicación

Identity


---

# 9. Organización del Repositorio

SST/
│
├── .github/
├── backend/
├── database/
├── docs/
├── infrastructure/
├── mobile/
├── web/
│
├── README.md
└── .gitignore


Cada carpeta tendrá una responsabilidad claramente definida.

| Carpeta | Responsabilidad |
|----------|-----------------|
| backend | API y lógica del negocio |
| database | Scripts y documentación de base de datos |
| docs | Documentación del proyecto |
| infrastructure | Docker, despliegue y configuración |
| mobile | Aplicación móvil |
| web | Aplicación web |

---

# 10. Organización del Backend

backend/
│
├── src/
│
├── API/
│
├── Modules/
│
├── Shared/
│
└── tests/


---

## API

Contendrá la configuración del servidor, autenticación, middlewares y exposición de endpoints.

---

## Modules

Contendrá todos los módulos del negocio.

Cada módulo será independiente y representará un dominio funcional.

---

## Shared

Contendrá componentes reutilizables.

Ejemplos:

- Kernel
- Archivos
- Notificaciones
- Auditoría
- Infraestructura compartida

---

## Tests

Contendrá las pruebas automatizadas.

---

# 11. Organización por Dominios

Los módulos se organizarán siguiendo el modelo del negocio.

Ejemplo preliminar:


Modules/

Personas/

Seguridad/

SaludOcupacional/

Capacitaciones/

Documentacion/

EPP/

Comunicacion/

Identity/


Esta organización podrá evolucionar conforme avance el descubrimiento del dominio.

---

# 12. Comunicación entre Componentes

La comunicación seguirá el siguiente flujo.


Usuario

↓

Aplicación Web / Mobile

↓

API REST

↓

Casos de Uso

↓

Dominio

↓

Persistencia

↓

PostgreSQL


Las aplicaciones cliente nunca accederán directamente a la base de datos.

---

# 13. Seguridad

La seguridad del sistema estará basada en:

- JWT
- Roles
- Permisos
- Autorización por políticas
- HTTPS
- Validación de datos en la API

---

# 14. Filosofía del Proyecto

Las decisiones arquitectónicas seguirán siempre el siguiente orden:

Negocio

↓

Procesos

↓

Casos de Uso

↓

Modelo del Dominio

↓

API

↓

Base de Datos

↓

Código
s

Nunca al revés.

---

# 15. Restricciones Arquitectónicas

Durante el desarrollo del proyecto se deberán respetar las siguientes restricciones:

- No utilizar Microservicios.
- No organizar módulos por pantallas.
- No duplicar lógica del negocio.
- No generar entidades innecesarias.
- No diseñar la base de datos antes del dominio.
- No escribir código sin haber definido previamente el modelo del negocio.
- Toda decisión deberá estar justificada.

---

# 16. Estado Actual del Proyecto

Actualmente se encuentran definidas las siguientes decisiones arquitectónicas:

- Arquitectura Cliente-Servidor.
- API REST.
- Monolito Modular.
- Clean Architecture.
- Organización por dominios del negocio.
- PostgreSQL como base de datos.
- JWT para autenticación.
- Roles y Permisos para autorización.
- Repositorio único en GitHub.

Las siguientes etapas del proyecto serán:

1. Descubrimiento del dominio.
2. Modelado del dominio.
3. Definición de procesos.
4. Casos de uso.
5. Diagramas UML.
6. Diseño de la API.
7. Diseño de la base de datos.
8. Implementación.

---

# 17. Conclusión

La arquitectura del Sistema SST está orientada a construir una solución empresarial, modular y mantenible, priorizando la simplicidad sobre la complejidad innecesaria.

Todas las decisiones futuras deberán alinearse con los principios establecidos en este documento, garantizando que el crecimiento del sistema no comprometa su organización ni su calidad técnica.