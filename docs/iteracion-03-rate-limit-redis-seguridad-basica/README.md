# iteracion-03-rate-limit-redis-seguridad-basica

## Objetivo De La Iteracion

Introducir rate limiting distribuido con Redis y hardening de seguridad basica en backend, de forma incremental y medible, para reducir riesgo de abuso y mejorar resiliencia operativa sin sobreingenieria.

## Alcance

- medir baseline de trafico y endpoints sensibles
- definir politica de limites por tipo de endpoint y actor (IP o usuario)
- agregar Redis al stack backend para soporte de rate limiting
- implementar filtro de rate limit con respuesta estandar 429
- endurecer headers HTTP de seguridad en configuracion
- externalizar CORS por entorno
- ajustar manejo de errores para no filtrar informacion sensible
- agregar tests de rate limit y seguridad, y actualizar quality gates

## Fuera De Alcance

- event driven architecture
- rabbitmq/kafka
- oauth provider externo (keycloak/auth0)
- observabilidad avanzada con prometheus/grafana
- extraccion de microservicios

## Estado De Fases

- [ ] Fase 1 - presion y politica de rate limit: [README de fase](fase-1-presion-y-politica-rate-limit/README.md)
- [ ] Fase 2 - implementacion de rate limit con redis: [README de fase](fase-2-implementacion-rate-limit-redis/README.md)
- [ ] Fase 3 - seguridad basica: [README de fase](fase-3-seguridad-basica/README.md)
- [ ] Fase 4 - validacion y rollout: [README de fase](fase-4-validacion-y-rollout/README.md)

## Desglose Por Fase

| Fase                                            | Rango de tareas | README de fase                                                                                       | Estado |
| ----------------------------------------------- | --------------- | ---------------------------------------------------------------------------------------------------- | ------ |
| Fase 1 - presion y politica de rate limit       | 1.1 a 1.3       | [fase-1-presion-y-politica-rate-limit/README.md](fase-1-presion-y-politica-rate-limit/README.md)     | [ ]    |
| Fase 2 - implementacion de rate limit con redis | 2.1 a 2.3       | [fase-2-implementacion-rate-limit-redis/README.md](fase-2-implementacion-rate-limit-redis/README.md) | [ ]    |
| Fase 3 - seguridad basica                       | 3.1 a 3.3       | [fase-3-seguridad-basica/README.md](fase-3-seguridad-basica/README.md)                               | [ ]    |
| Fase 4 - validacion y rollout                   | 4.1 a 4.3       | [fase-4-validacion-y-rollout/README.md](fase-4-validacion-y-rollout/README.md)                       | [ ]    |

## Riesgos Y Mitigaciones

- Riesgo: umbrales agresivos que bloqueen trafico legitimo.
  Mitigacion: rollout gradual por endpoint con feature flags y monitoreo.
- Riesgo: caida de Redis degrade disponibilidad de API.
  Mitigacion: politica de fallback documentada y comportamiento controlado ante fallo.
- Riesgo: hardening de seguridad impacte integraciones existentes.
  Mitigacion: validar compatibilidad por entorno y agregar pruebas de contrato de seguridad.

## Criterio De Cierre De Iteracion

- [ ] todas las fases cerradas
- [ ] todas las tareas verificadas
- [ ] limites de rate definidos y aplicados en endpoints objetivo
- [ ] respuesta 429 estandar validada por tests
- [ ] controles de seguridad basica activos por configuracion
- [ ] quality gates actualizados y en verde
