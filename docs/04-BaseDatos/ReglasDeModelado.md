# Plan de Implementación: Dashboard, Estadísticas, Filtro por Sede y Edición de Perfil

Este plan detalla los cambios técnicos para implementar las nuevas características en el sistema de seguridad y salud en el trabajo (SST).

## Cambios Propuestos

### 1. Filtrado por Sede (Sede Isolation)
Los supervisores (y trabajadores) solo deben ver información asociada a su sede. El Administrador puede ver toda la información de todas las sedes.

- **Identificación de la Sede del Usuario Logueado**:
  En el backend, se verificará el rol del usuario autenticado. Si no es `ADMINISTRADOR`, se buscará el perfil del trabajador asociado a su ID de usuario para obtener su `sede_id`.
- **Modificación en Repositorios**:
  Añadir métodos de consulta filtrados por `sedeId` en:
  - `TrabajadorRepository`
  - `CapacitacionRepository`
  - `InspeccionRepository`
- **Modificación en Servicios**:
  Actualizar los servicios para que llamen a las consultas filtradas cuando el usuario logueado no sea un Administrador.

---

### 2. Módulo de Dashboard (Panel de Control)
Crear un endpoint en el backend `/api/v1/dashboard` que retorne estadísticas agregadas:
- Cantidad total de trabajadores, capacitaciones e inspecciones.
- Gráficos y distribuciones:
  - Trabajadores por sede (solo visible para el Administrador).
  - Trabajadores por cargo.
  - Capacitaciones por estado y por tipo.
  - Inspecciones por estado y por tipo.
- Actividad reciente (últimas capacitaciones e inspecciones registradas).
- Próximos eventos (capacitaciones e inspecciones futuras ordenadas por fecha).

En el frontend, se reemplazará la pantalla de inicio estática (`/home`) por un Dashboard dinámico que muestre estas métricas de forma visual y atractiva.

---

### 3. Edición de Perfil (Usuario y Contraseña)
Modificar el formulario de Perfil (`/profile`) para que el usuario pueda cambiar sus credenciales de acceso:
- **Usuario (Correo Corporativo)**: Permitir la edición de este campo y validar que no esté duplicado en el sistema.
- **Contraseña**: Añadir un campo opcional para cambiar la contraseña (encriptándola con BCrypt en el backend).
- **Mantenimiento de Sesión**: Si el usuario cambia su correo corporativo, se re-generará el token JWT y se enviará de vuelta al frontend para actualizar la sesión de manera transparente.

---

### 4. Generación de Estadísticas y Reportes
Habilitar la pestaña de **Estadísticas** (`/reportes`) que actualmente está vacía.
- Mostrar paneles gráficos con la distribución de trabajadores, asistencia a capacitaciones y estado de inspecciones.
- Permitir la descarga de reportes detallados en formato **CSV** directamente desde el frontend:
  1. *Reporte de Trabajadores por Sede y Cargo*
  2. *Reporte de Cumplimiento de Capacitaciones*
  3. *Reporte de Estado de Inspecciones*

---

### 5. Ordenamiento de Capacitaciones e Inspecciones
Asegurar que todas las listas de capacitaciones e inspecciones (incluyendo las filtradas por sede) muestren primero los registros más recientes (orden descendente por fecha).

---

## Modificaciones de Archivos Detalladas

### Backend

#### [NEW] [DashboardController.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/dashboard/controller/DashboardController.java)
Controlador REST para exponer los datos del Dashboard y estadísticas.

#### [NEW] [DashboardStatsResponse.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/dashboard/dto/DashboardStatsResponse.java)
DTO que transporta los contadores, agrupaciones, actividad reciente y próximos eventos.

#### [NEW] [DashboardService.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/dashboard/service/DashboardService.java)
Definición del servicio del Dashboard.

#### [NEW] [DashboardServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/dashboard/service/impl/DashboardServiceImpl.java)
Implementación del servicio con las consultas agrupadas para armar las estadísticas.

#### [MODIFY] [ApiPaths.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/config/constants/ApiPaths.java)
Agregar ruta del endpoint del Dashboard.

#### [MODIFY] [TrabajadorRepository.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/trabajadores/repository/TrabajadorRepository.java)
Añadir consultas para listar por sede y buscar por sede.

#### [MODIFY] [TrabajadorServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/trabajadores/service/impl/TrabajadorServiceImpl.java)
Interceptar y aplicar filtro de sede si el usuario logueado no es administrador.

#### [MODIFY] [CapacitacionRepository.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/capacitaciones/repository/CapacitacionRepository.java)
Agregar consulta de capacitaciones asociadas a trabajadores de una sede específica.

#### [MODIFY] [CapacitacionServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/capacitaciones/service/impl/CapacitacionServiceImpl.java)
Filtrar listados por sede en base al usuario logueado.

#### [MODIFY] [InspeccionRepository.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/inspecciones/repository/InspeccionRepository.java)
Agregar consulta de inspecciones con responsables de una sede específica.

#### [MODIFY] [InspeccionServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/inspecciones/service/impl/InspeccionServiceImpl.java)
Filtrar listados por sede en base al usuario logueado.

#### [MODIFY] [UpdateProfileRequest.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/identity/dto/UpdateProfileRequest.java)
Agregar campos de `email` y `password` al request.

#### [MODIFY] [UserProfileResponse.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/identity/dto/UserProfileResponse.java)
Agregar campo `token` opcional para retornar JWT actualizado.

#### [MODIFY] [UserServiceImpl.java](file:///d:/PROYECTOS/SST/backend/src/main/java/pe/edu/sst/backend/modules/identity/service/impl/UserServiceImpl.java)
Actualizar correo corporativo y contraseña del usuario con encriptación, verificando unicidad del correo.

### Frontend

#### [MODIFY] [user-profile.model.ts](file:///d:/PROYECTOS/SST/web/src/app/features/profile/models/user-profile.model.ts)
Agregar campos opcionales `correoCorporativo`, `password` y `token` a la firma del request/response del perfil.

#### [MODIFY] [profile.component.ts](file:///d:/PROYECTOS/SST/web/src/app/features/profile/pages/profile/profile.component.ts)
Hacer editable el correo corporativo y agregar el campo opcional de contraseña al formulario. Enviar cambios al backend y refrescar tokens en localStorage si corresponde.

#### [MODIFY] [profile.component.html](file:///d:/PROYECTOS/SST/web/src/app/features/profile/pages/profile/profile.component.html)
Ajustar los inputs del formulario y añadir la sección de credenciales de seguridad (Usuario y Contraseña).

#### [NEW] [dashboard.service.ts](file:///d:/PROYECTOS/SST/web/src/app/features/dashboard/services/dashboard.service.ts)
Servicio Angular para consumir el endpoint `/api/v1/dashboard`.

#### [MODIFY] [home.component.ts](file:///d:/PROYECTOS/SST/web/src/app/features/dashboard/pages/home/home.component.ts)
Consumir `DashboardService` y proveer señales de estadísticas, actividad reciente y próximos eventos.

#### [MODIFY] [home.html](file:///d:/PROYECTOS/SST/web/src/app/features/dashboard/pages/home/home.html)
Renderizar dinámicamente las tarjetas de totales, listados y gráficos de barra sencillos (usando CSS puro/flexbox) para las distribuciones de trabajadores e inspecciones.

#### [MODIFY] [reportes-main.component.ts](file:///d:/PROYECTOS/SST/web/src/app/features/reportes/pages/reportes-main/reportes-main.component.ts)
Agregar lógica para visualizar las métricas consolidadas del dashboard y generar descargas en CSV de trabajadores, capacitaciones e inspecciones.

#### [MODIFY] [reportes-main.component.html](file:///d:/PROYECTOS/SST/web/src/app/features/reportes/pages/reportes-main/reportes-main.component.html)
Diseño de la página de reportes con tarjetas estadísticas, visualizaciones rápidas y botones de descarga de reportes.

---

## Plan de Verificación

### Pruebas Automatizadas
Para verificar el correcto funcionamiento del backend y evitar regresiones:
1. Compilar el proyecto completo:
   `mvn clean test` (en el directorio `backend`)

### Verificación Manual
1. **Verificación de Perfil**:
   - Iniciar sesión con un usuario.
   - Ir a Mi Perfil y cambiar el correo corporativo y/o la contraseña.
   - Verificar que al recargar la página el cambio persistió y la sesión sigue activa.
   - Intentar usar un correo que ya existe y comprobar que se muestra el mensaje de error.
2. **Verificación de Aislamiento de Sede**:
   - Iniciar sesión como Supervisor (el cual debe pertenecer a una sede específica, por ejemplo "Sede Sur").
   - Verificar que en el listado de Trabajadores, Capacitaciones e Inspecciones solo aparezcan los registros asociados a su sede.
   - Iniciar sesión como Administrador y validar que se visualicen los registros de todas las sedes.
3. **Verificación de Dashboard y Reportes**:
   - Validar que los números correspondan con la base de datos para la sede del supervisor o para todas las sedes del administrador.
   - Hacer clic en descargar reportes y verificar que los archivos CSV tengan el formato adecuado y los registros correspondientes.
