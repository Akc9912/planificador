# iteracion-02-hardening-arquitectonico-backend

## Objetivo De La Iteracion

Consolidar la arquitectura modular por capas ya migrada en Iteracion 01, endureciendo contratos, validaciones, guardrails y mantenibilidad para reducir riesgo de regresion y preparar una base estable para evolucion de stack.

## Alcance

- completar perfiles locales de arquitectura y stack como fuente de verdad operativa
- alinear documentacion principal con estado tecnico real del backend
- reforzar reglas de dependencia por capa en tests de arquitectura
- estandarizar validacion de entrada en capa presentation con Jakarta Validation
- estandarizar contrato de errores de API en formato JSON
- refactor focalizado del modulo Subject para reducir complejidad en capa application
- ejecutar regresion de suites existentes y nuevas pruebas de arquitectura/contrato

## Fuera De Alcance

- redis y rate limiting
- mensajeria o procesamiento async
- cambios a microservicios o extraccion de servicios
- observabilidad avanzada (tracing distribuido)
- cambios de frontend

## Estado De Fases

- [ ] Fase 1 - gobernanza y alineacion: [README de fase](fase-1-gobernanza-y-alineacion/README.md)
- [ ] Fase 2 - guardrails de arquitectura: [README de fase](fase-2-guardrails-arquitectura/README.md)
- [ ] Fase 3 - contratos y validacion: [README de fase](fase-3-contratos-y-validacion/README.md)
- [ ] Fase 4 - refactor focalizado de subject: [README de fase](fase-4-refactor-focalizado-subject/README.md)

## Desglose Por Fase

| Fase                                    | Rango de tareas | README de fase                                                                               | Estado |
| --------------------------------------- | --------------- | -------------------------------------------------------------------------------------------- | ------ |
| Fase 1 - gobernanza y alineacion        | 1.1 a 1.3       | [fase-1-gobernanza-y-alineacion/README.md](fase-1-gobernanza-y-alineacion/README.md)         | [ ]    |
| Fase 2 - guardrails de arquitectura     | 2.1 a 2.3       | [fase-2-guardrails-arquitectura/README.md](fase-2-guardrails-arquitectura/README.md)         | [ ]    |
| Fase 3 - contratos y validacion         | 3.1 a 3.3       | [fase-3-contratos-y-validacion/README.md](fase-3-contratos-y-validacion/README.md)           | [ ]    |
| Fase 4 - refactor focalizado de subject | 4.1 a 4.3       | [fase-4-refactor-focalizado-subject/README.md](fase-4-refactor-focalizado-subject/README.md) | [ ]    |

## Riesgos Y Mitigaciones

- Riesgo: sobre-refactor en modulo Subject sin ganancia tangible.
  Mitigacion: aplicar refactor incremental guiado por metrica de complejidad y pruebas.
- Riesgo: endurecer reglas de arquitectura y romper build por deuda previa.
  Mitigacion: introducir validaciones por capas en pasos pequenos y con feedback temprano en CI.
- Riesgo: cambios de contrato de error afecten consumidores.
  Mitigacion: versionar contrato de error y validar con pruebas de controlador.

## Criterio De Cierre De Iteracion

- [ ] todas las fases cerradas
- [ ] todas las tareas verificadas
- [ ] documentacion principal y perfiles user-\* actualizados
- [ ] 0 violaciones de boundaries por modulo y por capa
- [ ] contrato de validacion y errores estandarizado
- [ ] regresion completa en verde
