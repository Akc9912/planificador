# fase-3-migracion-modulos-soporte

## Contexto De La Fase

Luego de estabilizar modulos mas sensibles, se completa la alineacion de los modulos soporte para cerrar coherencia arquitectonica del backend.

## Objetivo De La Fase

Migrar Equivalence y Auth a capas target, y consolidar contratos/eventos entre modulos.

## Alcance De La Fase

- migrar modulo Equivalence a estructura de capas objetivo
- migrar modulo Auth a estructura de capas objetivo
- consolidar reglas de comunicacion por shared/api y shared/event

## Tareas De La Fase

- [ ] [3.1 Migrar Equivalence a capas target](3.1.md)
- [ ] [3.2 Migrar Auth a capas target](3.2.md)
- [ ] [3.3 Consolidar contratos y eventos entre modulos](3.3.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] Equivalence y Auth en capas target
- [ ] comunicacion entre modulos solo por contratos/eventos
- [ ] suite existente de modulos soporte en verde
- [ ] estado de fase actualizado en README de iteracion
