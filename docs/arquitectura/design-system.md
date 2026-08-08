# Plan de Implementación: Layout Fijo, Rate Limiting, RBAC y Módulo de Trabajadores

## Resumen del Problema y Objetivos
El usuario ha solicitado un conjunto de mejoras estructurales y funcionales:
1. **Layout Fijo**: Fijar el Header en la parte superior y el Sidebar en el lateral izquierdo sin que se desplacen al hacer scroll.
2. **Rate Limiting**: Implementar control de tasa de peticiones en el backend (Spring Boot) para prevenir spam/abuso.
3. **Control de Acceso por Roles (RBAC)**:
   - Acceso Web exclusivo para `ADMINISTRADOR` y `SUPERVISOR`. Los usuarios con rol `TRABAJADOR` están restringidos a la App móvil.
   - `ADMINISTRADOR`: Permiso de lectura y escritura (crear, modificar, cambiar estado).
   - `SUPERVISOR`: Permiso de solo lectura.
4. **Módulo de Trabajadores**:
   - Seeders de base de datos (`V4__seed_trabajadores.sql`) con registros de prueba representativos.
   - Listado paginado de trabajadores en el Backend y Frontend (búsqueda, filtros por sede/área, paginador).
   - Modal/Drawer de detalle del trabajador con botones de inspección rápida para "Capacitaciones" y "Documentos".

---

## Cambios Propuestos

### 1. Layout Fijo (Frontend Angular)

#### [MODIFY] [main-layout.component.scss](file:///d:/PROYECTOS/SST/web/src/app/layouts/main-layout/main-layout.component.scss)
- Configurar `.layout__content` con `margin-left` adaptativo (260px expandido / 80px colapsado) para acomodar el sidebar fijo.

#### [MODIFY] [header.component.scss](file:///d:/PROYECTOS/SST/web/src/app/layouts/main-layout/components/header/header.component.scss)
- Establecer `.header` como `position: sticky; top: 0; z-index: 90;` para fijar el menú superior.

#### [MODIFY] [sidebar.component.scss](file:///d:/PROYECTOS/SST/web/src/app/layouts/main-layout/components/sidebar/sidebar.component.scss)
- Establecer `.sidebar` como `position: fixed; top: 0; left: 0; bottom: 0; z-index: 100;`.

---

### 2. Rate Limiting y Seguridad RBAC (Backend Spring Boot)

#### [NEW] [RateLimitingFilter.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/security/filter/RateLimitingFilter.java)
- Filtro `OncePerRequestFilter` con algoritmo Token Bucket por IP/Usuario (límite de 60 peticiones/minuto). Retorna HTTP 429 Too Many Requests si se sobrepasa.

#### [MODIFY] [SecurityConfig.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/security/config/SecurityConfig.java)
- Registrar `RateLimitingFilter` antes de `JwtAuthenticationFilter`.
- Configurar autorizaciones por rol:
  - `POST`, `PUT`, `PATCH`, `DELETE` en `/api/v1/trabajadores/**`: Requiere `hasRole('ADMINISTRADOR')`.
  - `GET` en `/api/v1/trabajadores/**`: Requiere `hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')`.

#### [MODIFY] [AuthService.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/identity/service/AuthService.java)
- Bloquear login en la Web si el rol es `TRABAJADOR`, retornando mensaje descriptivo.

---

### 3. Base de Datos (Seeders Flyway)

#### [NEW] [V4__seed_trabajadores.sql](file:///d:/PROYECTOS/SST/backend/src/main/resources/db/migration/V4__seed_trabajadores.sql)
- Insertar 15 trabajadores de prueba distribuidos en diversas sedes, áreas y cargos con estados `ACTIVO` e `INACTIVO`.

---

### 4. Módulo de Trabajadores (Backend y Frontend)

#### [MODIFY] [TrabajadorService.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/trabajadores/service/TrabajadorService.java) y [TrabajadorServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/trabajadores/service/impl/TrabajadorServiceImpl.java)
- Implementar filtrado con búsqueda por texto (nombre/documento) y paginación `Pageable`.

#### [NEW] Módulo de Trabajadores en Angular (`src/app/features/trabajadores/`)
- [NEW] [`trabajador.model.ts`](file:///d:/PROYECTOS/SST/web/src/app/features/trabajadores/models/trabajador.model.ts): Interfaces de datos y respuestas paginadas.
- [NEW] [`trabajador.service.ts`](file:///d:/PROYECTOS/SST/web/src/app/features/trabajadores/services/trabajador.service.ts): Servicio HTTP para listar con paginación y obtener detalle.
- [NEW] [`trabajadores-list.component.ts/html/scss`](file:///d:/PROYECTOS/SST/web/src/app/features/trabajadores/pages/trabajadores-list/trabajadores-list.component.ts): Vista principal con tabla, controles de paginación, filtros de búsqueda, comprobación de rol del usuario y modal/drawer para inspección de perfil, capacitaciones y documentos del trabajador.

#### [MODIFY] [app.routes.ts](file:///d:/PROYECTOS/SST/web/src/app/app.routes.ts) y [menu-items.ts](file:///d:/PROYECTOS/SST/web/src/app/layouts/main-layout/constants/menu-items.ts)
- Enlazar la ruta `/trabajadores` en la navegación del sistema.

---

## Plan de Verificación

### Pruebas Automatizadas
1. **Compilación Backend**: Ejecuación de `./mvnw test-compile` en `backend/`.
2. **Compilación Frontend**: Ejecución de `npm run build` en `web/`.

### Verificación Manual / Funcional
1. Probar fijación de Header y Sidebar al desplazar el scroll en el navegador.
2. Verificar que las peticiones excesivas retornen HTTP 429.
3. Probar inicio de sesión con Administrador vs Supervisor, verificando la restricción de botones de edición según el rol.
4. Navegar a la sección de Trabajadores, cambiar de página y abrir el modal de detalle del trabajador para inspeccionar capacitaciones y documentos.
