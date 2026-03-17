# Fase 3 - Modulos Complementarios y Auditoria

## Objetivo de fase

Incorporar los modulos complementarios del backend y el sistema de auditoria sin romper el aislamiento modular alcanzado en las fases anteriores.

## Estado actual segun el plan

Event y Reminder quedaron desacoplados del MVP actual y sus endpoints legacy responden `410 GONE`. Esta fase documenta como reintroducirlos de forma correcta cuando llegue su turno, junto con UserSettings y Audit.

## Tareas de la fase

- [ ] [3.1 Implementar Event Module](./3.1.md)
- [ ] [3.2 Implementar Reminder Module](./3.2.md)
- [ ] [3.3 Implementar UserSettings Module](./3.3.md)
- [ ] [3.4 Implementar Audit Module](./3.4.md)
- [ ] [3.5 Integraciones Entre Modulos](./3.5.md)

## Checklist de avance

- [ ] 3.1 Event vuelve como modulo nuevo, no como rehabilitacion legacy.
- [ ] 3.2 Reminder vuelve como modulo nuevo, no como rehabilitacion legacy.
- [ ] 3.3 UserSettings queda resuelto con defaults y ownership por usuario.
- [ ] 3.4 Audit captura acciones criticas a nivel aplicacion.
- [ ] 3.5 Las integraciones cruzadas usan eventos o interfaces compartidas.

## Criterio de salida de la fase

- Los cuatro modulos complementarios existen en estructura modular real.
- Event y Reminder dejan de depender del comportamiento `410 GONE` porque ya tienen modulo nuevo probado.
- Audit registra operaciones criticas sin ensuciar controladores.
- Ninguna vinculacion entre modulos se resuelve con imports directos.
