### Cuando haga: ## significa submodulo , # pagina anidada de una funcion (estos tipos suelen repetirse en otros modulos o submodulos, tener en cuenta diferenciar por datos)

## Trabajadores activos
-- Titulo : Lista de Colaboradores
-- Descarga Excel:
|| Tipo de Documento || Nro Documento || Nombre || Unidad || Area || Sede ||
-- Tabla:  
|| Tipo de Documento || Nro Documento || Nombre || Unidad || Area || Sede || Acciones||
--Boton Acciones: Dirige hacia detalle Colaborador
# Detalle del Colaborador 
-- Titulo: Detalle del Colaborador 
-- Opciones de Dirección: RACs Inspecciones EMOs Capacitaciones Accidentes/Incidentes Documentos Notas 
--Opciones de funcionalidad: Regresar GuardarCambios
--Se muestra en pantalla bloques de informacion para añadir
--BLOQUE Datos Personales: Nombres, Apellido Paterno, Apellido Materno, Tipo de Documento (DNI, PASAPORTE, CARNE DE EXTRANJERIA, RUC), Nro de Documento, Fecha de Nacimiento, Sexo, Correo Electronico Personal, correo Electronico corporativo, Registrar firma digital (imagen), Telefono, Pais, Departamento, Provincia, Distrito, Direccion
--BLOQUE Datos de Contacto: Nombres, Telefono
--BLOQUE Datos Laborales: Jefe Directo, Sede, Unidad (ej: Finanzas,Operaciones, Ventas), Área (ej: Finanzas, operaciones, seguridad, marketing), Puesto, Centro de Costos, Nivel de Exposicion (Muy alta, alta, media, baja, muy baja), Tipo de Usuario, Seguro de atención médica, Fecha de ingreso, Modalidad de Contrato (ej: Remoto, Practicante,presencial), Gerencia, Razon Social, Puesto de capacitación, Protocolos de EMO, Empresas
# RACs
-- Titulo: Actos y Condiciones subestándar
--Opciones de dirección: Reporte Configuración AccionesCorrectivas AgregarHallazgo
-- Se muestra: 
||Reporte y ubicación || Hallazgo y gravedad || Medidas ||Estado|| Agregar Hallazgo||
# RACs / Reporte 
-- Titulo: Reporte de Actos y Condiciones 
-- Diagramas Power By:
-- Filtros: Fecha de Inicio , Fecha Final, Sede , Area
-- Diagrama Circular Hallazgos: Acto inseguro, Condicion insegura %
-- Diagrama circular Nivel de Riesgo: Alto, Medio, Muy Bajo, Bajo %
-- Diagrama Total RACs por Mes: No hay diagrama visible
-- Diagrama Acciones Correctivas por Estado: No hay diagrama visible
-- Diagrama Histograma "Donde detecto el Acto y/o Condicion": Sede que reporta, Sede que se detecta, en los ejes, esta Sede, Cantidad, Es un diagrama de barras donde la cantidad de la sede que se reporta es lineal y de barras es la cantidad que se detecta en la sede
-- Diagrama de Barras "Turno donde se detecto la ocurrencia": Total, Turno, (Mañana, Tarde, Noche)
-- Diagrama "Área que detecta vs Área que reporta": Area que reporta es lineal y Area que se detecta es de barras. Ejes: Area, Cantidad
-- Tabla "Datos generales de las ocurrencias":
|Mes de Ocurrencia|Tipo de Ocurrencia|Lo dectado me podría generar|Donde detecto el Acto y/o Condición| Area donde se detecto | Fecha del Reporte |Turno|Area que reporta la ocurrencia| Observaciones|
-- Tabla "Detalle de Ocurrencia"
|Fecha de Ocurrencia| Hor de Ocurrencia|Tipo de Ocurrencia|Sede donde se detecto|Usuario que hizo el registro| Ubicación Detallada|Lo detectado me podría generar |Area de la incidencia| Fecha de registro |Estado del Hallazgo|Persona que cerró el Hallazgo| Numero de Documento del que registro| Acciones Tomadas| URL imagen 1,2,3,4,5,6| Responsable sugerido de la accion correctiva| Aspectos de la gestion| Que se hizo al respecto| Estado Evaluación| Sustento de Evaluación | Personal que Evaluó| Fecha de cierre del Hallazgo| Fecha de evaluación|
-- Tabla "Seguimiento de ejecuciones de acciones correctivas"
|DNI|Nombre de la persona que ejecuta las acciones correctivas|Año| Mes|Cantidad de ejecuciones de acciones correctivas|
-- Tabla "Seguimiento de Medidas Correctivas Generada "
|DNI|Persona que generó la medida correctiva|Año|Mes|Cantidad de Medidas Correctivas|
# RACs / Configuración 
-- Titulo: Configuración RACs 
-- Funcionalidad: Regresar
--Tipos de RACs, configurar: Nombre (Acto inseguro), Descripcion, Imagenes, esto se puede guardar
--En los Tipos hay: Acto Inseguro, Condición insegura, Tiendas - Actos inseguros/subEstándares,  Cento de Distribución - Operación de màquinas montacargas - Actos Inseguros /subestàndares, Fallas en el equipo
--Bloque Gestion de Hallazgos detectados:
Nombre, Categoria, Riesgo
--Bloque Responsables: Se genera una notificación a los responsables
Buscamos Al responsable: Tipo de Documento, Numero de documento
--Bloque "Usuarios alertados ante un Hallazgos" : Supuestamente si surge ese Hallazgo predeterminado se le avisa a estos usuarios
Buscar Usuario: Filtros -> Area, Sede 
--Bloque "Usuarios responsables ante un hallazgo":
Buscar Usuario: Filtros -> Area, Sede 
# RACs/ Acciones correctivas
-Titulo: Consolidado de acciones correctivas
-Funcionalidades: Regresar
-Tabla de visualización: 
||Tipo||Titulo||Fecha Plazo||Fecha Aprobacion||Responsable||Estado (Atrasado, Por Aprobar, Aprobado, ...)||Acciones||
-En Acciones (Funcionalidad de dirección) : Visualización, Levantamiento
# RACs/ Acciones correctivas / Accion* (Depende de la función, pero lo bloques son los mismos para todo)
-Bloque de información: 
Funcionalidad: Levantanmiento (Es el mismo codigo html y accion que en el anterio caso)
-Bloque "Información de Hallazgo Original": Ver Reporte Racs (Funcionalidad de Direccion), Fecha de reporte, Area / Sede, Lugar exacto
-Bloque "Trazabilidad de cambios y Estados": Fecha, Responsable, Mensaje "Se registro la tarea en el sistema derivado del hallazgoen campo"
-Bloque "Designación de Responsables": Nombre o DNI, buscar y agregar
-Bloque "Información de la Acción": Clasificación de Acción, Fecha de Registro, Fecha de Plazo Limite, Titulo, Descripcion
--TODO esto permite tanto modificar dandole a guardar o eliminarlo
# RACs/ Acciones correctivas / Accion* (Depende de la función, pero lo bloques son los mismos para todo)/ Ver Reporte RACs 
-Titulo: "Detalle de Hallazgos": Codigo: 2026-057
-Bloque de informacion:
ESTADO: (Abierto, Cerrado), Evaluación del reporte (Desaprobado, Aprobado), Fecha de Evaluación, Sustento de la evaluación
-Bloque "Reporte de Condición insegura" : Tipo, Fecha, Hora, Area, Sede, Razon Social, Nivel de riesgo, Fecha de Creación, 
    Registrado por: Nombres y Apellidos, Correo Electronico, Telefono, Nº Documento, Sexo, Fecha de Nacimiento, Pais, Departamento, Provincia, Distrito, Unidad, Area, Sede, Puesto, Centro de Costos, Jefe Directo
    Descripción:
    Descripción de acción correctiva
    Condición Insegura detectó
    Fotos adjuntas, videos, archivo
Esto se puede eliminar o actualizar
-Exportar Registro:
Titulo:Reporte de Hallazgo
Fecha, Version, Pagina, codigo
Datos de Hallazgo: Fecha, Hora, Nº: Sede/Sucursal , Area/Empresa , Lugar
Marcar Tipo: Acto Inseguro, Centro de distribucion .... , Condicion insegura, Fallos en el equipo, Tiendas
Descripcion
AccionInmediata tomada descripcion
Reportado Por
Cargo
Persona Reportada
Cargo de Persona Reportada
# RACs/ Acciones correctivas / Accion* (Depende de la función, pero lo bloques son los mismos para todo)/ Ver Reporte RACs / Ver acciones Correctivas
--Ver acciones Correctivas
#Titulo: Consolidado de acciones correctivas
Fecha , Lugar, Descripcion
Listado de Acciones: Titulo, Fecha Plazo, Fecha Ejecución, Fecha Aprobación, Responsable, Estado, Acciones (Ver detalle, Levantamiento), el ver Detalle envia al mismo lugar que Accion*
--Bloque Levantamiento : Fecha de Ejecucion, descripcion de lo realido, Evidencia imagen o pdf
Se puede exportar en Excel, además de Agregar nueva Accion
# RACs/ Acciones correctivas / Accion* (Depende de la función, pero lo bloques son los mismos para todo)/ Ver Reporte RACs / Ver acciones Correctivas/ Agregar nueva Acción
Titulo: Agregar nueva accion correctiva
-Bloque "Información del hallazgo original":
Fecha de Reporte, Area/Sede, Lugar exacto
-Bloque "Designación de Responsables"
Buscar Nombre o DNI
-Bloque "Informacion de la accion"
Clasificacion de accion, Fecha de registro, fecha de plazo, titulo, descripcion/observaciones
# RACs/ Agregar Hallazgo
De los tipos de Hallazgos: Acto Inseguro, Condicion insegura, Tiendas, Centro de distribucion, Fallas en el equipo

Titulo : Acto inseguro
¿Aquien observo? Descripcion (uno o varios),  ¿Donde ocurrió?, Área, Direccion o lugar Detalle, Fecha, Hora, Observacion Detalle, Nivel de Riesgo, ¿Que ocurrió?, ¿Qué hizo al respecto?, ¿Qué recomiendas para prevenir la observación detectada?, Adjuntar imagenes, video, archivo.

Titulo: Reporte de Condición Insegura
¿Donde ocurrió?, Area, Fecha, Hora, Observación detalle, Nivel de riesgo, ¿Qué recomendaria para prevenir?, Adjuntar fotos, videos, archivos

Para los demás Es lo mismo.

# Inspecciones
-Titulo: Listado de inspecciones
-Funciones de Direccion: Configuracion, Reporte, Acciones correctivas, Programar inspecciones
-Muestra un bloque de informacion
Objeto Mostrado:
|CODIGO, NOMBRE, AREA| RESPONSABLE, F. PROGAMADA| PROGRESO %| Estado| ACCIONES|
-En Acciones hay: Exportar Registro, Ver acciones correctivas, Ver detalle

-Accion "Exportar Registro": 
Titulo: Inspeccion de equipos de Protección Personal
Inspeccionado POR: , CARGO, FECHA, HORA
LISTA DE EQUIPOS DE PROTECCIÓN PERSONAL
Apellidos y Nombre||Area de Trabajo||Firma||Observaciones 
Firma del Inspector

-Accion "Ver acciones": Direcciona a Listado de Medidas Correctivas - Especifico 
# Inspecciones/ Ver acciones
Fecha de la Inspeccion, Tipo de la inspeccion, Formulario de la inspeccion, Area/Sede, Objetivo de la Inspeccion
-Tabla de Listado de medidas Correctivas - Especifico
||Titulo||Fecha Plazo|| Fecha Ejecucion|| Fecha Aprobacion|| Responsable|| Estado|| Acciones||
Se puede exportar en una matriz de excel 
# Inspecciones/ Detalle de la Inspeccion
Titulo: Dellate de la Inspección 
Codigo, Estado, Fecha de actualización y hora
-Bloque "Datos de la Inspección"
Tipo de Inspeccion, Fecha de programacion, Hora programacion Razon Social, Sede, Area/Lugar, Formulario, Grupo, Objetivo de la inspeccion
-Bloque Resumen Tocara ver como funciona esto, debido a que al parecer es como un porcentaje de items auditados aprobados, además señala la cantidad de Hallazgos
-Bloque Asignación de Responsable
Nombres y Apellidos, correo, telefono, sexo, fecha nacimiento, Pais, departamento, Provincia Distrito, Unidad, Area, Sede,Puesto, Centro de costos, Jefe Directo (Mucha de esta info solo debe aparecer al registrar y no creo que sea necesario todo) Se puede agregar varios responsables a la inspeccion
-Bloque Adjuntos:
Nro, Fecha Registro, Titulo, Registrado Por, Acciones, En esta parte solo se agrega documentos creo y la tabla es solo visualizar, nada de actualizar, solo subir, se pone nombre y el documento adjunto.

---------- De aqui siguen las funciones de dirección de Inspecciones: Configuracion, Reporte, Acciones Correctivas, Programar inspeccion
# Inspecciones / Configuración
-Bloque Grupo
Nombre del grupo, integrantes del grupo, Esto esta mal planteado pero supuestamente asigna algunas tareas a un grupo de caracteristicas, (SST, Ambiente, Operaciones ...), los cuales perteneceran a un responsable y ya creo
-Bloque "Biblioteca de Plantillas":
Nombre de la inspección| Descripción| Fecha de Creación|Creado por |Estado|Acciones|
--Funcionalidad de Agregar Plantilla:
Nombre de la inspección, Codigo de documento, Fecha de version, Nro de Version, Formato PDF base, Descripción del Formulario, Hay un boton de agregar Seccion, y otro punto que es 2. "Definir Secciones y Preguntas", pero no permite hacer nada, luego sale guardar Formulario
# Inspecciones / Reporte
Titulo: Reporte de Inspecciones
--Diagrama Circular "Status de Inspecciones" (Pendiente, Realizada, Por Firmar)
--Diagraam de barras "Por Razon Social" (Realizada, Por Firmar, Pendiente), vs Cantidad
-Tabla Listado de Inspecciones: Objetivo, TIPO, Area, Razon social, Responsable, Fecha de Programacion, Estado, Nro Observaciones
-Diagrama de barras "Nro de Observaciones por Área de Trabajo": Area vs Total
-Diagrama de barras "Nro de Medidas Correctivas por Área de Trabajo": Area (Aprobado, Abierto) vs Cantidad
-Tabla Lista de Medidas Correctivas: Inspeccion Descripcion, Medidad Correctiva Descripcion, Responsable, Fecha Ejecucion, Fecha de Programacion, Estado (Atrasado, Por Aprobar)
# Inspecciones / Listado de Medidas Correctivas
--Es lo mismo a Acciones Correctivas, solo cambia de nombre, igual con la medidad correctiva espefica, solo añade una funcionalidad más en Acciones  que es observacion
||Titulo||Fecha Plazo||Fecha Ejecucion||Fecha Aprobacion||Responsable||Estado (Atrasado, Por Aprobar, Aprobado, ...)||Acciones||
La funcionalidad Observacion solo genera para ver el detalle: Criterio, Observacion, Imagenes
# Inspecciones / Programar Inspección
Titulo: Agregar Nueva inspección 
Codigo
-Bloque "Datos de la Inspección"
Tipo de inspección (Planeado, no Planeado), Fecha programacion, Hora programacion, razon social, sede, Area, formulario (El conjunto de acciones mencionados que hemos configurado), Objetivo de la inspeccion
-Bloque "Asignación del Responsable"
Sería solo buscar al responsable por nombre o dni, se puede agregar más responsables
#
## Capacitaciones
## Examenes médicos
## Vigilancia médica
## Subsidios y Recuperos
## Equipos de protección 
## Charlas 5 minutos
## Pausas Activas
## Difusión de documentos
