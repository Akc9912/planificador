# Fase 2 - MVP - Modulos Core

## Objetivo de fase

Cerrar los modulos core del MVP y su logica de negocio principal: Career, Subject, Equivalence y Auth, con validaciones de dominio, endpoints utiles y pruebas suficientes para sostener el backend fuera de legacy.

## Estado actual segun el plan

Career y Auth ya tienen avance real. Career queda cerrado en la tarea 2.1 y Subject queda implementado/cerrado en 2.2 con evidencia de auditoria y pruebas en verde. Equivalence sigue como siguiente frente tecnico.

## Estado real auditado del repo - 17/03/2026

- `modules/career` queda cerrado como modulo plantilla del MVP: ownership por token, contrato compartido (`CareerApi`), evento de dominio (`CareerDeletedEvent`) y pruebas de servicio/controlador en verde.
- `modules/subject` queda implementado en UUID sobre `subjects`, `subject_modules` y `subject_schedules`, con CRUD modular, ownership via `CareerApi`, cleanup por evento y suite propia en verde.
- `modules/auth` existe con controlador, servicio, filtro JWT y pruebas propias en verde.
- No hay archivos implementados aun dentro de `src/main/java/aktech/planificador/modules/equivalence`.
- No aparece evidencia actual de progreso cerrado para correlativas, dashboard, busqueda avanzada o modulo de equivalencias.

## Nota de numeracion

Esta fase mezcla modulos prioritarios y bloques de logica critica en el plan original. Para cumplir el formato documental, se asigna una numeracion lineal 2.1 a 2.9 respetando ese orden operativo.

## Tareas de la fase

- [x] [2.1 Implementar Career Module](./2.1.md) - Cerrada: modulo Career auditado y validado (estructura, ownership, contrato, eventos y pruebas).
- [x] [2.2 Implementar Subject Module](./2.2.md) - Cerrada: modulo Subject implementado sobre UUID con CRUD, ownership por contrato compartido y pruebas en verde.
- [ ] [2.3 Implementar Equivalence Module](./2.3.md) - Pendiente real: el modulo no tiene implementacion efectiva en `src/main/java`.
- [x] [2.4 Implementar Auth Module](./2.4.md)
- [ ] [2.5 Validacion de Correlativas](./2.5.md) - Pendiente.
- [ ] [2.6 Calculo de Progreso](./2.6.md) - Pendiente.
- [ ] [2.7 Validaciones de Negocio](./2.7.md) - Parcial bajo: existen validaciones puntuales, pero no una cobertura cerrada para Subject y Equivalence.
- [ ] [2.8 Busqueda y Filtros](./2.8.md) - Pendiente.
- [ ] [2.9 Endpoints de Dashboard](./2.9.md) - Pendiente.

## Checklist de avance

- [x] 2.1 Career queda cerrado como modulo plantilla.
- [x] 2.2 Subject entra en UUID y sin imports a legacy.
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
