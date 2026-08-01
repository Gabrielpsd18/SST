# Estructura del Repositorio

| Documento | Estructura del Repositorio |
|------------|----------------------------|
| Proyecto | Sistema de Seguridad y Salud en el Trabajo (SST) |
| Versión | 1.0 |
| Estado | En diseño |
| Última actualización | Agosto 2026 |

---

# 1. Propósito

Este documento describe la organización del repositorio del proyecto SST.

Su objetivo es establecer una estructura clara, escalable y mantenible que facilite el desarrollo, la documentación y la evolución del sistema.

La estructura ha sido diseñada considerando que el proyecto será desarrollado inicialmente por un único desarrollador, pero con criterios propios de un sistema empresarial.

---

# 2. Principios de Organización

La organización del repositorio sigue los siguientes principios:

- Separación clara de responsabilidades.
- Un único repositorio para todo el sistema.
- Independencia entre Backend, Web y Mobile.
- Documentación centralizada.
- Infraestructura desacoplada del código.
- Facilidad para incorporar nuevas aplicaciones en el futuro.

---

# 3. Estructura General

```text
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
├── .gitignore
└── README.md
```

---

# 4. Descripción de Carpetas

## .github/

Contiene la configuración relacionada con GitHub.

Ejemplos:

- GitHub Actions
- Plantillas de Issues
- Pull Requests
- Configuración del repositorio

Ejemplo:

```text
.github/

workflows/

ISSUE_TEMPLATE/

PULL_REQUEST_TEMPLATE.md
```

---

## backend/

Contiene todo el código del Backend.

Incluye:

- API REST
- Lógica del negocio
- Casos de uso
- Persistencia
- Seguridad
- Módulos del sistema

Toda la lógica del negocio deberá implementarse aquí.

---

## database/

Contiene todos los recursos relacionados con la base de datos.

Ejemplo:

```text
database/

scripts/

migrations/

seed/

diagramas/
```

Podrá incluir:

- Scripts SQL
- Migraciones
- Datos iniciales
- Diagramas
- Diccionario de datos

No contendrá código del Backend.

---

## docs/

Contiene toda la documentación del proyecto.

Ejemplo:

```text
docs/

00-Vision/

01-Arquitectura/

02-Dominio/

03-CasosDeUso/

04-Requisitos/

05-API/

06-UML/

07-BaseDatos/

08-ADR/
```

Toda decisión importante deberá documentarse aquí antes de implementarse.

---

## infrastructure/

Contiene recursos necesarios para ejecutar o desplegar el sistema.

Ejemplos:

- Docker
- Docker Compose
- Nginx
- CI/CD
- Kubernetes (si en un futuro fuera necesario)
- Scripts de despliegue

Ejemplo:

```text
infrastructure/

docker/

nginx/

scripts/

deployment/
```

No contendrá lógica del negocio.

---

## mobile/

Contiene la aplicación móvil.

Incluye:

- Código fuente
- Recursos
- Configuración
- Pruebas

Consumirá exclusivamente la API REST.

Nunca accederá directamente a la base de datos.

---

## web/

Contiene la aplicación Web.

Incluye:

- Código fuente
- Componentes
- Recursos
- Configuración

Consumirá exclusivamente la API REST.

Nunca contendrá lógica del negocio.

---

# 5. Archivos Principales

## README.md

Documento principal del proyecto.

Debe incluir:

- descripción del sistema
- tecnologías utilizadas
- estructura del proyecto
- instrucciones de instalación
- guía rápida para desarrolladores

---

## .gitignore

Define los archivos que no deben almacenarse en el repositorio.

Ejemplos:

- bin/
- obj/
- node_modules/
- archivos temporales
- configuraciones locales
- secretos

---

# 6. Relaciones entre Carpetas

```text
                +----------------+
                |     docs       |
                +-------+--------+
                        |
                        |
        +---------------+----------------+
        |                                |
        ▼                                ▼

  backend                        database

        ▲                                ▲
        |                                |
        +---------------+----------------+
                        |
                        ▼
               infrastructure

        ▲
        |
        |
+-------+--------+
|                |
▼                ▼

web           mobile
```

La documentación describe el sistema.

El Backend implementa la lógica.

La Base de Datos almacena la información.

Las aplicaciones Web y Mobile consumen la API.

La infraestructura permite ejecutar y desplegar el sistema.

---

# 7. Flujo General

```text
Usuario

↓

Web / Mobile

↓

API REST (Backend)

↓

Lógica del Negocio

↓

PostgreSQL
```

---

# 8. Reglas de Organización

Durante todo el proyecto deberán respetarse las siguientes reglas.

## Backend

- Toda la lógica del negocio pertenece al Backend.
- No implementar lógica de negocio en Web ni Mobile.
- No acceder directamente a la base de datos desde los clientes.

---

## Web

- Solo consume la API.
- No contiene reglas del negocio.
- No accede directamente a PostgreSQL.

---

## Mobile

- Solo consume la API.
- Comparte las mismas reglas funcionales que la aplicación Web.
- No accede directamente a PostgreSQL.

---

## Database

- Solo contiene recursos relacionados con la base de datos.
- No almacena código de la aplicación.

---

## Documentation

- Toda decisión arquitectónica deberá documentarse.
- Ninguna funcionalidad importante deberá implementarse sin haber sido previamente analizada.

---

# 9. Convenciones

La estructura del repositorio deberá mantenerse estable durante todo el proyecto.

No deberán crearse carpetas nuevas sin una necesidad claramente justificada.

Cada carpeta deberá tener una única responsabilidad.

Se evitarán estructuras duplicadas.

---

# 10. Evolución del Repositorio

La organización propuesta está preparada para crecer de forma incremental.

En el futuro podrán incorporarse nuevos componentes, por ejemplo:

```text
SST/

backend/

web/

mobile/

desktop/

integrations/

database/

docs/

infrastructure/
```

La incorporación de nuevos proyectos no deberá afectar la estructura existente.

---

# 11. Beneficios de esta Organización

Esta estructura proporciona las siguientes ventajas:

- Separación clara de responsabilidades.
- Fácil navegación del proyecto.
- Bajo acoplamiento entre aplicaciones.
- Documentación centralizada.
- Escalabilidad.
- Mantenibilidad.
- Facilidad para incorporar nuevos desarrolladores.
- Despliegues independientes de cada componente.

---

# 12. Conclusión

La estructura del repositorio ha sido diseñada para soportar el desarrollo de un sistema empresarial de Seguridad y Salud en el Trabajo (SST), manteniendo una organización simple, consistente y escalable.

Cada componente del repositorio tiene una responsabilidad específica, permitiendo que el proyecto evolucione de forma ordenada y facilitando el mantenimiento a largo plazo.