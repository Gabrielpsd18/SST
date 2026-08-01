# ADR-001 - Arquitectura Monolito Modular

| Estado | Aprobado |
|---------|----------|
| Fecha | Agosto 2026 |
| Decisión | Utilizar una arquitectura Monolito Modular |

---

# Contexto

El sistema SST será desarrollado inicialmente por un único desarrollador y estará compuesto por un Backend, una aplicación Web y una aplicación Mobile.

Se requiere una arquitectura que permita crecer de forma ordenada sin introducir una complejidad innecesaria.

---

# Decisión

Se utilizará una **arquitectura Monolito Modular**, donde el Backend será una única aplicación desplegable, organizada en módulos independientes según los dominios del negocio.

Ejemplo:

```text
Modules/

Personas/

Seguridad/

SaludOcupacional/

Capacitaciones/

Documentacion/

EPP/

Identity/
```

Cada módulo encapsulará su propia lógica de negocio y colaborará con los demás mediante contratos bien definidos.

---

# Justificación

Se eligió esta arquitectura porque:

- Reduce la complejidad respecto a los microservicios.
- Facilita el desarrollo por un único desarrollador.
- Favorece la mantenibilidad y la organización del código.
- Permite evolucionar el sistema de forma modular.
- Simplifica el despliegue y la administración.

---

# Consecuencias

## Ventajas

- Un único despliegue.
- Menor complejidad operativa.
- Organización por dominios del negocio.
- Fácil refactorización entre módulos.
- Menor costo de mantenimiento.

## Desventajas

- Todo el Backend se despliega como una sola aplicación.
- Requiere disciplina para evitar el acoplamiento entre módulos.

---

# Alternativas Consideradas

## Microservicios

**Descartado** por aumentar significativamente la complejidad de desarrollo, despliegue y mantenimiento para el alcance actual del proyecto.

## Monolito Tradicional

**Descartado** porque favorece el crecimiento desordenado del código y dificulta la separación de responsabilidades.

---

# Resultado

El Backend del sistema SST será implementado como un **Monolito Modular**, organizado por dominios del negocio y siguiendo los principios de Clean Architecture.