# Fase 2 - MVP - Modulos Core

## Objetivo de fase

Cerrar los modulos core del MVP y su logica de negocio principal: Career, Subject, Equivalence y Auth, con validaciones de dominio, endpoints utiles y pruebas suficientes para sostener el backend fuera de legacy.

## Estado actual segun el plan

Career y Auth ya tienen avance real. Career queda cerrado en la tarea 2.1, Subject queda cerrado en 2.2 y Equivalence queda implementado/cerrado en 2.3 con evidencia de auditoria y pruebas en verde.

## Estado real auditado del repo - 17/03/2026

- `modules/career` queda cerrado como modulo plantilla del MVP: ownership por token, contrato compartido (`CareerApi`), evento de dominio (`CareerDeletedEvent`) y pruebas de servicio/controlador en verde.
- `modules/subject` queda implementado en UUID sobre `subjects`, `subject_modules` y `subject_schedules`, con CRUD modular, ownership via `CareerApi`, reglas de correlativas (2.5), progreso academico (2.6), validaciones de dominio (2.7) y busqueda/filtros minimos con orden (2.8).
- `modules/equivalence` queda implementado en UUID con CRUD y validaciones de negocio activas (circularidad, carreras distintas y duplicados directos/inversos) por contratos compartidos de Subject.
- `modules/auth` queda cerrado en 2.4 con contrato modular documentado (`/auth/me`, `/auth/token/validate`) y delegacion a Supabase en `410 GONE` para credenciales.
- No aparece evidencia actual de cierre para dashboard.

## Nota de numeracion

Esta fase mezcla modulos prioritarios y bloques de logica critica en el plan original. Para cumplir el formato documental, se asigna una numeracion lineal 2.1 a 2.9 respetando ese orden operativo.

## Tareas de la fase

- [x] [2.1 Implementar Career Module](./2.1.md) - Cerrada: modulo Career auditado y validado (estructura, ownership, contrato, eventos y pruebas).
- [x] [2.2 Implementar Subject Module](./2.2.md) - Cerrada: modulo Subject implementado sobre UUID con CRUD, ownership por contrato compartido y pruebas en verde.
- [x] [2.3 Implementar Equivalence Module](./2.3.md) - Cerrada: modulo Equivalence implementado con reglas de negocio y pruebas propias en verde.
- [x] [2.4 Implementar Auth Module](./2.4.md) - Cerrada: contrato auth modular documentado y suite auth/seguridad validada (`29 tests`, `0 failures`).
- [x] [2.5 Validacion de Correlativas](./2.5.md) - Cerrada: reglas de bloqueo/habilitacion implementadas en SubjectService y validadas con 23 tests en verde.
- [x] [2.6 Calculo de Progreso](./2.6.md) - Cerrada: DTO + endpoint de progreso por carrera en Subject con metricas y 26 tests en verde.
- [x] [2.7 Validaciones de Negocio](./2.7.md) - Cerrada: reglas de transicion en Subject + validaciones de Equivalence consolidadas con 48 tests en verde.
- [x] [2.8 Busqueda y Filtros](./2.8.md) - Cerrada: endpoint de busqueda por carrera con filtros (nombre, codigo, estado, anio, semestre) y orden validado, con 36 tests en verde.
- [ ] [2.9 Endpoints de Dashboard](./2.9.md) - Pendiente.

## Checklist de avance

- [x] 2.1 Career queda cerrado como modulo plantilla.
- [x] 2.2 Subject entra en UUID y sin imports a legacy.
- [x] 2.3 Equivalence queda acoplado solo por contratos compartidos.
- [x] 2.4 Auth queda estable y documentado para handoff de contrato.
- [x] 2.5 Correlativas calculan bloqueos y habilitaciones.
- [x] 2.6 Progreso y estadisticas se recalculan correctamente.
- [x] 2.7 Reglas de dominio quedan en servicios y con pruebas.
- [x] 2.8 Hay busqueda, filtros y ordenamientos minimos.
- [ ] 2.9 El backend expone datos utiles para dashboard.

## Criterio de salida de la fase

- Career queda completamente cerrado.
- Auth y Career mantienen su suite en verde.
- Subject y Equivalence quedan implementados sobre esquema UUID.
- La logica critica de correlativas y progreso queda probada.
- Los nuevos modulos siguen la regla de no interaccion con codigo legacy.
