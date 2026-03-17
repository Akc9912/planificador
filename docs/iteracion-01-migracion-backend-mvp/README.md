# Iteracion 01 - Migracion Backend MVP

## Objetivo

Convertir el plan de migracion actual en una iteracion documental operativa, con fases y tareas ejecutables por cualquier colaborador nuevo sin depender de contexto oral.

## Fuentes base

- [MIGRATION_PLAN.md](../../MIGRATION_PLAN.md)
- [REGLAS_CREACION_ITERACIONES.MD](../REGLAS_CREACION_ITERACIONES.MD)
- [ARCHITECTURE.md](../../ARCHITECTURE.md)

## Alcance de esta iteracion

Esta iteracion cubre el backend MVP modular completo definido en el plan actual:

- Fase 1: setup y configuracion modular.
- Fase 2: modulos core del MVP.
- Fase 3: modulos complementarios y auditoria.
- Fase 4: soporte, endurecimiento y preparacion para produccion.

## Regla de normalizacion de numeracion

El plan fuente mezcla secciones numeradas y bloques operativos sin numeracion uniforme. Para cumplir el estandar documental, esta iteracion normaliza cada unidad operativa a formato `N.M`, sin cambiar el orden ni el alcance definido en `MIGRATION_PLAN.md`.

## Estado de fases

- [ ] [Fase 1 - Setup y configuracion modular](./fase-1-setup-y-configuracion-modular/README.md) - En progreso en el plan actual.
- [ ] [Fase 2 - MVP - Modulos core](./fase-2-mvp-modulos-core/README.md) - En progreso en el plan actual.
- [ ] [Fase 3 - Modulos complementarios y auditoria](./fase-3-modulos-complementarios-y-auditoria/README.md) - Pendiente.
- [ ] [Fase 4 - Soporte y produccion](./fase-4-soporte-y-produccion/README.md) - Pendiente.

## Desglose por fase

- Fase 1 - Tareas 1.1 a 1.7 - [README de fase](./fase-1-setup-y-configuracion-modular/README.md)
- Fase 2 - Tareas 2.1 a 2.9 - [README de fase](./fase-2-mvp-modulos-core/README.md)
- Fase 3 - Tareas 3.1 a 3.5 - [README de fase](./fase-3-modulos-complementarios-y-auditoria/README.md)
- Fase 4 - Tareas 4.1 a 4.5 - [README de fase](./fase-4-soporte-y-produccion/README.md)

## Reglas operativas de ejecucion

- Trabajar modulo por modulo hasta cerrar cada dominio antes de abrir el siguiente.
- Cerrar Career por completo antes de avanzar Subject y Equivalence.
- El codigo nuevo no debe interactuar con codigo legacy.
- No agregar tests nuevos al codigo legacy.
- La migracion de UUID se ejecuta de forma incremental por modulo.
- El backend permanece fuera de produccion hasta completar el MVP modular con testing suficiente.
- La adaptacion del frontend queda fuera de la ejecucion tecnica de esta iteracion, salvo preparacion de contratos para handoff.

## Criterio general de uso

1. Abrir el README de la fase correspondiente.
2. Ejecutar las tareas en orden.
3. Marcar cada tarea cuando tenga evidencia tecnica verificable.
4. No saltar una tarea bloqueante sin dejar evidencia de decision.
5. Mantener build, guardrails y suite modular en verde al cerrar cada bloque.

## Checklist de validacion de la iteracion

- [x] Existe carpeta de iteracion con nombre valido.
- [x] Existe README en la iteracion.
- [x] Existe una carpeta por fase.
- [x] Existe README por fase con links a tareas.
- [x] Existe un archivo md por cada tarea planificada en esta iteracion documental.
- [ ] Todas las tareas estan cerradas con evidencia tecnica.
- [ ] Todas las fases tienen build y pruebas en verde al cierre.
- [ ] La documentacion refleja el estado real del backend luego de cada cierre.
