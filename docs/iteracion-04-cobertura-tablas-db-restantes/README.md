# iteracion-04-cobertura-tablas-db-restantes

## Objetivo De La Iteracion

Implementar las funcionalidades restantes del backend para cubrir todas las tablas pendientes del esquema de base de datos, manteniendo arquitectura modular por capas y ownership por usuario.

## Alcance

- cubrir tabla events con CRUD completo
- cubrir tabla event_schedules con CRUD y validacion de rango temporal
- cubrir tabla event_subjects para asociacion opcional de eventos con subjects
- cubrir tabla reminders con CRUD completo
- cubrir tabla reminder_subjects para asociacion opcional de recordatorios con subjects
- cubrir tabla user_settings con operaciones get/upsert
- cubrir tabla support_tickets con CRUD y flujo de estado
- cubrir tabla audit_logs para registro y consulta de auditoria
- ejecutar regresion completa y cierre formal de cobertura de tablas

## Fuera De Alcance

- microservicios y cambios de arquitectura distribuida
- mensajeria asincrona (rabbitmq/kafka)
- notificaciones push/email/sms
- observabilidad avanzada fuera de lo minimo operativo

## Estado De Fases

- [ ] Fase 1 - baseline y diseno db: [README de fase](fase-1-baseline-y-diseno-db/README.md)
- [ ] Fase 2 - modulo events: [README de fase](fase-2-modulo-events/README.md)
- [ ] Fase 3 - modulo reminders y settings: [README de fase](fase-3-modulo-reminders-settings/README.md)
- [ ] Fase 4 - soporte, auditoria y cierre: [README de fase](fase-4-soporte-auditoria-cierre/README.md)

## Desglose Por Fase

| Fase                                 | Rango de tareas | README de fase                                                                           | Estado |
| ------------------------------------ | --------------- | ---------------------------------------------------------------------------------------- | ------ |
| Fase 1 - baseline y diseno db        | 1.1 a 1.3       | [fase-1-baseline-y-diseno-db/README.md](fase-1-baseline-y-diseno-db/README.md)           | [ ]    |
| Fase 2 - modulo events               | 2.1 a 2.3       | [fase-2-modulo-events/README.md](fase-2-modulo-events/README.md)                         | [ ]    |
| Fase 3 - modulo reminders y settings | 3.1 a 3.3       | [fase-3-modulo-reminders-settings/README.md](fase-3-modulo-reminders-settings/README.md) | [ ]    |
| Fase 4 - soporte, auditoria y cierre | 4.1 a 4.3       | [fase-4-soporte-auditoria-cierre/README.md](fase-4-soporte-auditoria-cierre/README.md)   | [ ]    |

## Riesgos Y Mitigaciones

- Riesgo: inconsistencia entre ownership backend y politicas RLS de DB.
  Mitigacion: validar reglas de acceso por usuario en cada endpoint y pruebas de integracion.
- Riesgo: acoplamiento indebido entre nuevos modulos y modulos existentes.
  Mitigacion: mantener contratos via shared/api y reforzar ModuleBoundariesTest.
- Riesgo: cobertura parcial de tablas por priorizacion funcional.
  Mitigacion: checklist final tabla -> modulo -> endpoint -> test.

## Criterio De Cierre De Iteracion

- [ ] todas las fases cerradas
- [ ] todas las tareas verificadas
- [ ] tablas faltantes cubiertas por backend: events, event_schedules, event_subjects, reminders, reminder_subjects, user_settings, audit_logs, support_tickets
- [ ] regresion completa en verde
- [ ] documentacion actualizada
- [ ] metricas y evidencia registradas
