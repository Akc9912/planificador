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

- [x] [3.1 Migrar Equivalence a capas target](3.1.md)
- [x] [3.2 Migrar Auth a capas target](3.2.md)
- [x] [3.3 Consolidar contratos y eventos entre modulos](3.3.md)

## Checklist De Avance De Fase

- [x] todas las tareas listadas en esta fase estan creadas
- [x] cada tarea tiene comandos o verificacion explicita
- [x] no hay tareas fuera del rango definido para la fase
- [x] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [x] Equivalence y Auth en capas target
- [x] comunicacion entre modulos solo por contratos/eventos
- [x] suite existente de modulos soporte en verde
- [x] estado de fase actualizado en README de iteracion
