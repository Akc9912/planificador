# iteracion-01-implementacion-arquitectura-stack-backend

## Diagnostico De Presion Arquitectonica

| Tipo de presion | Evidencia actual                                           | Nivel | Respuesta en esta iteracion                    |
| --------------- | ---------------------------------------------------------- | ----- | ---------------------------------------------- |
| Dominio         | Servicios con alta concentracion de reglas en modulos core | Medio | separacion estricta por capas target           |
| Arquitectura    | Brecha entre estructura actual y modelo target de 5 capas  | Alto  | migracion modulo a modulo por criticidad       |
| Operacional     | Necesidad de trazabilidad formal para ejecucion ordenada   | Medio | documentacion completa de iteracion/fase/tarea |
| Stack base      | Requisito de Java 21 y Docker operativo                    | Alto  | estandarizar runtime y contenedor al inicio    |
| Performance     | Sin indicadores criticos actuales                          | Bajo  | no introducir stack nivel 1                    |

## Iteracion Activa

- Tipo: Architecture Evolution
- Sistema: backend unicamente
- Orden de criticidad de modulos: Career -> Subject -> Equivalence -> Auth
- Politica de testing: ejecutar y mantener en verde los tests existentes, sin agregar tests nuevos

## Arquitectura Actual

- Estilo vigente: Modular Monolith con organizacion Package by Feature.
- Estructura por modulo observada: controller, service, model, repository, dto (con variaciones como enums, filter o listener segun modulo).
- Configuracion y componentes transversales centralizados en paquetes comunes (Config y shared).
- Flujo predominante: controller -> service -> repository, con logica de negocio concentrada en service.
- Brecha principal: los modulos aun no exponen de forma explicita las 5 capas target (presentation, application, domain, persistence, infrastructure).

## Arquitectura Objetivo (Migracion)

- Estilo objetivo: Modular Monolith por modulos de dominio, manteniendo Package by Feature.
- Cada modulo debe quedar en 5 capas explicitas: presentation, application, domain, persistence, infrastructure.
- Reglas de dependencia a cumplir:
  - presentation -> application -> domain
  - application -> persistence
  - application -> infrastructure
- Restricciones de acoplamiento:
  - prohibido domain -> persistence o domain -> infrastructure
  - prohibido presentation -> persistence
- Comunicacion entre modulos solo por interfaces de aplicacion y eventos de dominio.
- Orden de migracion de esta iteracion: Career -> Subject -> Equivalence -> Auth.

## Objetivo De La Iteracion

Implementar la arquitectura backend definida y el stack base obligatorio (Java 21 + Docker), con migracion incremental por modulo y sin complejidad innecesaria.

## Alcance

- estandarizar Java 21 en proyecto y CI
- dejar Docker operativo para backend
- migrar modulos a estructura target de capas: presentation, application, domain, persistence, infrastructure
- mantener contratos actuales para evitar regresiones funcionales
- ejecutar pruebas existentes en cada cierre de fase

## Fuera De Alcance

- frontend
- microservicios
- redis, mensajeria, observabilidad avanzada
- nuevas tecnologias de stack nivel 1+
- creacion de tests nuevos

## Fases De La Iteracion

- [x] Fase 1 - baseline y stack: [README de fase](fase-1-baseline-y-stack/README.md)
- [ ] Fase 2 - migracion modulos criticos: [README de fase](fase-2-migracion-modulos-criticos/README.md)
- [ ] Fase 3 - migracion modulos soporte: [README de fase](fase-3-migracion-modulos-soporte/README.md)
- [ ] Fase 4 - cierre y validacion: [README de fase](fase-4-cierre-y-validacion/README.md)

## Subtareas Por Fase

### Fase 1

- [1.1 Baseline de arquitectura y contratos](fase-1-baseline-y-stack/1.1.md)
- [1.2 Estandarizar Java 21 en backend y CI](fase-1-baseline-y-stack/1.2.md)
- [1.3 Docker backend operativo](fase-1-baseline-y-stack/1.3.md)
- [1.4 Evidencia de baseline y cierre de fase](fase-1-baseline-y-stack/1.4.md)

### Fase 2

- [2.1 Migrar Career a capas target](fase-2-migracion-modulos-criticos/2.1.md)
- [2.2 Migrar Subject a capas target](fase-2-migracion-modulos-criticos/2.2.md)
- [2.3 Regresion existente de modulos criticos](fase-2-migracion-modulos-criticos/2.3.md)

### Fase 3

- [3.1 Migrar Equivalence a capas target](fase-3-migracion-modulos-soporte/3.1.md)
- [3.2 Migrar Auth a capas target](fase-3-migracion-modulos-soporte/3.2.md)
- [3.3 Consolidar contratos y eventos entre modulos](fase-3-migracion-modulos-soporte/3.3.md)

### Fase 4

- [4.1 Regresion completa de tests existentes](fase-4-cierre-y-validacion/4.1.md)
- [4.2 Verificacion final de arquitectura y stack](fase-4-cierre-y-validacion/4.2.md)
- [4.3 Cierre de iteracion y backlog siguiente](fase-4-cierre-y-validacion/4.3.md)

## Desglose Por Fase

| Fase                                | Rango de tareas | README de fase                                                                             | Estado |
| ----------------------------------- | --------------- | ------------------------------------------------------------------------------------------ | ------ |
| Fase 1 - baseline y stack           | 1.1 a 1.4       | [fase-1-baseline-y-stack/README.md](fase-1-baseline-y-stack/README.md)                     | [x]    |
| Fase 2 - migracion modulos criticos | 2.1 a 2.3       | [fase-2-migracion-modulos-criticos/README.md](fase-2-migracion-modulos-criticos/README.md) | [ ]    |
| Fase 3 - migracion modulos soporte  | 3.1 a 3.3       | [fase-3-migracion-modulos-soporte/README.md](fase-3-migracion-modulos-soporte/README.md)   | [ ]    |
| Fase 4 - cierre y validacion        | 4.1 a 4.3       | [fase-4-cierre-y-validacion/README.md](fase-4-cierre-y-validacion/README.md)               | [ ]    |

## Riesgos Y Mitigaciones

- Riesgo: refactor estructural con regresion funcional.
  Mitigacion: migracion incremental por modulo con validacion de tests existentes.
- Riesgo: sobredimensionar stack antes de necesitarlo.
  Mitigacion: restringir iteracion a stack base nivel 0.
- Riesgo: desalineacion entre arquitectura objetivo y estado real.
  Mitigacion: checklist de capa por modulo y evidencia por subtarea.
- Riesgo: cambios de runtime sin consistencia CI.
  Mitigacion: estandarizar Java 21 en fase 1 antes de migrar modulos.

## Criterios De Aceptacion Y Metricas

Criterios:

- [ ] todos los modulos del alcance cumplen estructura target de capas
- [ ] Java 21 activo en proyecto y CI
- [ ] Docker backend operativo
- [ ] tests existentes en verde al cierre de cada fase
- [ ] documentacion completa de iteracion/fases/tareas

Metricas:

- 0 fallos en suite existente definida por fase
- 0 imports prohibidos entre modulos o hacia legacy
- 100% de tareas con evidencia de verificacion
- 0 tecnologias nuevas fuera de stack nivel 0

## Siguiente Paso Recomendado

Ejecutar la tarea [2.1 Migrar Career a capas target](fase-2-migracion-modulos-criticos/2.1.md) para iniciar migracion de modulos criticos.

## Criterio De Cierre De Iteracion

- [ ] todas las fases cerradas
- [ ] todas las tareas verificadas
- [ ] arquitectura y stack base alineados
- [ ] evidencia registrada en documentacion
