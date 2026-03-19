# Changelog

Todos los cambios notables de este proyecto se documentan en este archivo.

El formato esta inspirado en Keep a Changelog y versionado semantico (SemVer).

## [1.0.0] - 2026-03-19

### Added

- Arquitectura Modular Monolith por dominio para `Auth`, `Career`, `Subject` y `Equivalence`.
- Estructura por capas explicitas en modulos: `presentation`, `application`, `domain`, `persistence`, `infrastructure`.
- Validacion automatizada de limites arquitectonicos entre modulos con `ModuleBoundariesTest`.
- Estandarizacion del runtime backend en Java 21 y stack base Spring Boot 3.5.4.
- Soporte de contenedor para backend con flujo Docker operativo.
- Documentacion trazable de ejecucion por iteracion, fase y tarea para la salida de la version inicial.

### Changed

- Reorganizacion de paquetes internos desde estructura legacy a arquitectura modular por capas.
- Consolidacion de contratos compartidos y eventos en paquetes `shared/*` para reducir acoplamiento.
- Alineacion del pipeline de calidad para compilar y validar arquitectura modular en CI.

### Security

- Validacion de JWT de Supabase en backend y autorizacion basada en rol de usuario.
- Refuerzo de aislamiento entre modulos para evitar imports directos no permitidos.

### Testing

- Regresion de pruebas existente ejecutada en verde al cierre de la release (`123/123` segun evidencia de Iteracion 01).
- Verificacion de cero violaciones de boundaries modulares al cerrar la version.
