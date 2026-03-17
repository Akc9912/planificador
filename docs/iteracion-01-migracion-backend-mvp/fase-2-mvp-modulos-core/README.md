# Fase 2 - MVP - Modulos Core

## Objetivo de fase

Cerrar los modulos core del MVP y su logica de negocio principal: Career, Subject, Equivalence y Auth, con validaciones de dominio, endpoints utiles y pruebas suficientes para sostener el backend fuera de legacy.

## Estado actual segun el plan

Career y Auth ya tienen avance real. Subject y Equivalence siguen pendientes como siguiente frente tecnico. La prioridad operativa vigente es cerrar Career por completo antes de abrir Subject y Equivalence.

## Nota de numeracion

En `MIGRATION_PLAN.md`, esta fase mezcla modulos prioritarios y bloques de logica critica. Para cumplir el formato documental, se asigna una numeracion lineal 2.1 a 2.9 respetando ese orden operativo.

## Tareas de la fase

- [ ] [2.1 Implementar Career Module](./2.1.md)
- [ ] [2.2 Implementar Subject Module](./2.2.md)
- [ ] [2.3 Implementar Equivalence Module](./2.3.md)
- [ ] [2.4 Implementar Auth Module](./2.4.md)
- [ ] [2.5 Validacion de Correlativas](./2.5.md)
- [ ] [2.6 Calculo de Progreso](./2.6.md)
- [ ] [2.7 Validaciones de Negocio](./2.7.md)
- [ ] [2.8 Busqueda y Filtros](./2.8.md)
- [ ] [2.9 Endpoints de Dashboard](./2.9.md)

## Checklist de avance

- [ ] 2.1 Career queda cerrado como modulo plantilla.
- [ ] 2.2 Subject entra en UUID y sin imports a legacy.
- [ ] 2.3 Equivalence queda acoplado solo por contratos compartidos.
- [ ] 2.4 Auth queda estable y documentado para handoff de contrato.
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
