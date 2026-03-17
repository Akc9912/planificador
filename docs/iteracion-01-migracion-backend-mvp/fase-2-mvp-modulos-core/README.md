# Fase 2 - MVP - Modulos Core

## Objetivo de fase

Cerrar los modulos core del MVP y su logica de negocio principal: Career, Subject, Equivalence y Auth, con validaciones de dominio, endpoints utiles y pruebas suficientes para sostener el backend fuera de legacy.

## Estado actual segun el plan

Career y Auth ya tienen avance real. Subject y Equivalence siguen pendientes como siguiente frente tecnico. La prioridad operativa vigente es cerrar Career por completo antes de abrir Subject y Equivalence.

## Estado real auditado del repo - 17/03/2026

- `modules/career` existe con capas reales y pruebas propias en verde (`CareerServiceTest` y `CareerControllerTest`).
- `modules/auth` existe con controlador, servicio, filtro JWT y pruebas propias en verde.
- `modules/subject` solo tiene dos piezas base hoy: `SubjectCareerAccessService` y `CareerEventListener`.
- No hay archivos implementados aun dentro de `src/main/java/aktech/planificador/modules/equivalence`.
- No aparece evidencia actual de progreso cerrado para correlativas, dashboard, busqueda avanzada o modulo de equivalencias.

## Nota de numeracion

Esta fase mezcla modulos prioritarios y bloques de logica critica en el plan original. Para cumplir el formato documental, se asigna una numeracion lineal 2.1 a 2.9 respetando ese orden operativo.

## Tareas de la fase

- [ ] [2.1 Implementar Career Module](./2.1.md) - Parcial alto: modulo y pruebas presentes; falta tratarlo como completamente cerrado dentro de la iteracion.
- [ ] [2.2 Implementar Subject Module](./2.2.md) - Parcial inicial: solo existe base de acceso a Career y listener de evento.
- [ ] [2.3 Implementar Equivalence Module](./2.3.md) - Pendiente real: el modulo no tiene implementacion efectiva en `src/main/java`.
- [x] [2.4 Implementar Auth Module](./2.4.md)
- [ ] [2.5 Validacion de Correlativas](./2.5.md) - Pendiente.
- [ ] [2.6 Calculo de Progreso](./2.6.md) - Pendiente.
- [ ] [2.7 Validaciones de Negocio](./2.7.md) - Parcial bajo: existen validaciones puntuales, pero no una cobertura cerrada para Subject y Equivalence.
- [ ] [2.8 Busqueda y Filtros](./2.8.md) - Pendiente.
- [ ] [2.9 Endpoints de Dashboard](./2.9.md) - Pendiente.

## Checklist de avance

- [ ] 2.1 Career queda cerrado como modulo plantilla.
- [ ] 2.2 Subject entra en UUID y sin imports a legacy.
- [ ] 2.3 Equivalence queda acoplado solo por contratos compartidos.
- [x] 2.4 Auth queda estable y documentado para handoff de contrato.
- [ ] 2.5 Correlativas calculan bloqueos y habilitaciones.
- [ ] 2.6 Progreso y estadisticas se recalculan correctamente.
- [ ] 2.7 Reglas de dominio quedan en servicios y con pruebas.
- [ ] 2.8 Hay busqueda, filtros y ordenamientos minimos.
- [ ] 2.9 El backend expone datos utiles para dashboard.

## Criterio de salida de la fase

- Career queda completamente cerrado.
- Auth y Career mantienen su suite en verde.
- Subject y Equivalence quedan implementados sobre esquema UUID.
- La logica critica de correlativas y progreso queda probada.
- Los nuevos modulos siguen la regla de no interaccion con codigo legacy.
