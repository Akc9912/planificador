# Fase 4 - Soporte y Produccion

## Objetivo de fase

Completar el modulo final de soporte y dejar el backend listo para operar con mejores niveles de performance, seguridad, observabilidad y documentacion.

## Estado actual segun el plan

Esta fase sigue pendiente. Parte de la base tecnica ya existe, como Swagger y seguridad modular, pero todavia faltan optimizaciones, monitoreo y documentacion de salida a produccion.

## Estado real auditado del repo - 17/03/2026

- Existe configuracion Swagger/OpenAPI en `OpenApiConfig` y dependencia `springdoc-openapi-starter-webmvc-ui` en `pom.xml`.
- La base de seguridad modular ya existe: JWT, filtro auth y pruebas de seguridad en verde.
- No se detecta modulo `support` en `src/main/java/aktech/planificador/modules`.
- No aparece evidencia actual de `actuator` ni propiedades `management.*` para observabilidad.
- No aparece evidencia actual de cache o paginacion como parte cerrada de esta fase.

## Tareas de la fase

- [ ] [4.1 Implementar Support Module](./4.1.md) - Pendiente.
- [ ] [4.2 Optimizacion de Performance](./4.2.md) - Pendiente.
- [ ] [4.3 Hardening de Seguridad](./4.3.md) - Parcial: la base de seguridad existe, pero la fase de endurecimiento completo no esta cerrada.
- [ ] [4.4 Observabilidad](./4.4.md) - Pendiente real: no hay evidencia actual de Actuator o `management.*`.
- [ ] [4.5 Documentacion de Produccion](./4.5.md) - Parcial: Swagger/OpenAPI y docs base existen, pero no esta cerrada la salida documental de produccion.

## Checklist de avance

- [ ] 4.1 Existe modulo de soporte con ownership por usuario.
- [ ] 4.2 El backend reduce sobrecarga de consultas y listados.
- [ ] 4.3 Seguridad incorpora validaciones y headers adicionales.
- [ ] 4.4 Existen health checks, metricas y base de monitoreo.
- [ ] 4.5 Swagger, README y artefactos de integracion quedan listos.

## Criterio de salida de la fase

- El modulo de soporte esta operativo.
- El backend tiene optimizaciones y controles razonables para pasar a una etapa productiva.
- Existen health checks y metricas consultables.
- La documentacion final permite operar e integrar el backend sin depender del conocimiento del autor.
