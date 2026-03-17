# Iteracion 01 - Migracion Backend MVP

## Objetivo

Convertir el plan de migracion actual en una iteracion documental operativa, con fases y tareas ejecutables por cualquier colaborador nuevo sin depender de contexto oral.

## Fuentes base

- [REGLAS_CREACION_ITERACIONES.MD](../REGLAS_CREACION_ITERACIONES.MD)
- [ARCHITECTURE.md](../../ARCHITECTURE.md)

## Alcance de esta iteracion

Esta iteracion cubre el backend MVP modular completo definido en el plan actual:

- Fase 1: setup y configuracion modular.
- Fase 2: modulos core del MVP.
- Fase 3: modulos complementarios y auditoria.
- Fase 4: soporte, endurecimiento y preparacion para produccion.

## Regla de normalizacion de numeracion

El plan fuente mezcla secciones numeradas y bloques operativos sin numeracion uniforme. Para cumplir el estandar documental, esta iteracion normaliza cada unidad operativa a formato `N.M`, sin cambiar el orden ni el alcance definido en la fuente original.

## Estado de fases

- [x] [Fase 1 - Setup y configuracion modular](./fase-1-setup-y-configuracion-modular/README.md) - Cerrada: tareas 1.1 a 1.7 con build en verde, suite minima de 50 pruebas y documentacion alineada al backend real.
- [ ] [Fase 2 - MVP - Modulos core](./fase-2-mvp-modulos-core/README.md) - Avance real medio: Auth resuelto, Career muy avanzado, Subject parcial inicial, Equivalence y logica critica pendientes.
- [ ] [Fase 3 - Modulos complementarios y auditoria](./fase-3-modulos-complementarios-y-auditoria/README.md) - Aun no migrada; Event y Reminder siguen desacoplados via `410 GONE` en legacy.
- [ ] [Fase 4 - Soporte y produccion](./fase-4-soporte-y-produccion/README.md) - Aun no iniciada como fase; Swagger y base de seguridad ya existen, pero soporte, observabilidad y optimizacion siguen pendientes.

## Desglose por fase

- Fase 1 - Tareas 1.1 a 1.7 - [README de fase](./fase-1-setup-y-configuracion-modular/README.md)
- Fase 2 - Tareas 2.1 a 2.9 - [README de fase](./fase-2-mvp-modulos-core/README.md)
- Fase 3 - Tareas 3.1 a 3.5 - [README de fase](./fase-3-modulos-complementarios-y-auditoria/README.md)
- Fase 4 - Tareas 4.1 a 4.5 - [README de fase](./fase-4-soporte-y-produccion/README.md)

## Estado real auditado del repo - 17/03/2026

- `./mvnw -q -DskipTests compile` compila en verde.
- No se detectan errores activos en `src` desde el analizador del editor.
- La suite modular actual registrada en `target/surefire-reports` esta en verde: 2 pruebas de arquitectura, 22 de auth y 26 de career, todas sin errores ni fallos.
- Fase 1 queda formalmente cerrada con ese baseline de build y testing.
- Los modulos presentes hoy en `src/main/java/aktech/planificador/modules` son `auth`, `career`, `subject` y `equivalence`.
- `subject` solo tiene hoy una base inicial (`SubjectCareerAccessService` y `CareerEventListener`).
- No hay implementacion efectiva aun en `src/main/java/aktech/planificador/modules/equivalence`.
- Event y Reminder siguen fuera del MVP modular y sus controladores legacy devuelven `410 GONE`.
- Swagger/OpenAPI ya esta configurado con `springdoc-openapi-starter-webmvc-ui` y `OpenApiConfig`.

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
- [ ] Todas las tareas estan cerradas con evidencia tecnica.
- [ ] Todas las fases tienen build y pruebas en verde al cierre.
- [x] La documentacion refleja el estado real del backend luego de cada cierre.

## Siguiente frente tecnico

1. Verificar si queda algun borde menor de Career como modulo plantilla definitiva.
2. Despejado eso, abrir Subject dentro de Fase 2.
3. Dejar Equivalence para despues de Subject, sin reabrir definiciones base de Fase 1.
