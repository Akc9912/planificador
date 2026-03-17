# Iteracion 01 - Migracion Backend MVP

## Objetivo

Cerrar el MVP backend modular requerido en esta etapa (Fase 1 y Fase 2), dejando Fase 3 y Fase 4 como backlog para iteraciones futuras.

## Fuentes base

- [REGLAS_CREACION_ITERACIONES.MD](../REGLAS_CREACION_ITERACIONES.MD)
- [ARCHITECTURE.md](../../ARCHITECTURE.md)

## Alcance de esta iteracion

Esta iteracion queda cerrada con alcance MVP core:

- Fase 1: setup y configuracion modular.
- Fase 2: modulos core del MVP.
- Fase 3: modulos complementarios y auditoria (diferida a iteracion futura).
- Fase 4: soporte, endurecimiento y preparacion para produccion (diferida a iteracion futura).

## Regla de normalizacion de numeracion

El plan fuente mezcla secciones numeradas y bloques operativos sin numeracion uniforme. Para cumplir el estandar documental, esta iteracion normaliza cada unidad operativa a formato `N.M`, sin cambiar el orden ni el alcance definido en la fuente original.

## Estado de fases

- [x] [Fase 1 - Setup y configuracion modular](./fase-1-setup-y-configuracion-modular/README.md) - Cerrada: tareas 1.1 a 1.7 con build en verde, suite minima de 50 pruebas y documentacion alineada al backend real.
- [x] [Fase 2 - MVP - Modulos core](./fase-2-mvp-modulos-core/README.md) - Cerrada: tareas 2.1 a 2.9 completadas con evidencia tecnica y pruebas modulares en verde.
- [ ] [Fase 3 - Modulos complementarios y auditoria](./fase-3-modulos-complementarios-y-auditoria/README.md) - Diferida por alcance: pasa a iteracion futura, fuera del MVP requerido.
- [ ] [Fase 4 - Soporte y produccion](./fase-4-soporte-y-produccion/README.md) - Diferida por alcance: pasa a iteracion futura, fuera del MVP requerido.

## Desglose por fase

- Fase 1 - Tareas 1.1 a 1.7 - [README de fase](./fase-1-setup-y-configuracion-modular/README.md)
- Fase 2 - Tareas 2.1 a 2.9 - [README de fase](./fase-2-mvp-modulos-core/README.md)
- Fase 3 - Tareas 3.1 a 3.5 - [README de fase](./fase-3-modulos-complementarios-y-auditoria/README.md) (backlog para iteracion futura)
- Fase 4 - Tareas 4.1 a 4.5 - [README de fase](./fase-4-soporte-y-produccion/README.md) (backlog para iteracion futura)

## Estado real auditado del repo - 17/03/2026

- `./mvnw -q -DskipTests compile` compila en verde.
- Fase 1 y Fase 2 quedan cerradas para el alcance MVP actual.
- Los modulos core presentes en `src/main/java/aktech/planificador/modules` son `auth`, `career`, `subject` y `equivalence`.
- `subject` y `equivalence` quedan implementados en esquema UUID con reglas de negocio y endpoints del MVP.
- Event y Reminder siguen fuera del MVP modular y sus implementaciones legacy fueron eliminadas del repo para ser reimplementadas desde cero en una iteracion futura.
- Swagger/OpenAPI ya esta configurado con `springdoc-openapi-starter-webmvc-ui` y `OpenApiConfig`.
- Fase 3 y Fase 4 quedan explicitamente diferidas a iteraciones futuras por decision de alcance.

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
- [x] Existe un corte de estado real del repo incorporado a la iteracion.
- [x] Todas las tareas del alcance MVP (Fase 1 y Fase 2) estan cerradas con evidencia tecnica.
- [x] El corte de alcance deja Fase 3 y Fase 4 como backlog para nuevas iteraciones.
- [x] La documentacion refleja el estado real del backend luego de cada cierre.

## Siguiente frente tecnico

1. Abrir la siguiente iteracion con Fase 3 (Event, Reminder, UserSettings y Audit) como nuevo alcance.
2. Planificar Fase 4 (soporte y produccion) en una iteracion posterior al cierre de Fase 3.
3. Mantener la regla de no mezclar codigo nuevo con legacy en cada nueva migracion modular.
