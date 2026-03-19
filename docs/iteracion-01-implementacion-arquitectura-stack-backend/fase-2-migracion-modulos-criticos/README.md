# fase-2-migracion-modulos-criticos

## Contexto De La Fase

Con baseline y stack base resueltos, se migra primero la parte mas critica del dominio para cumplir arquitectura objetivo sin romper comportamiento.

## Objetivo De La Fase

Migrar Career y Subject a capas target con ejecucion incremental y regresion de tests existentes.

## Alcance De La Fase

- migrar modulo Career a presentation/application/domain/persistence/infrastructure
- migrar modulo Subject a presentation/application/domain/persistence/infrastructure
- ejecutar pruebas existentes de modulos criticos y boundaries

## Tareas De La Fase

- [x] [2.1 Migrar Career a capas target](2.1.md)
- [x] [2.2 Migrar Subject a capas target](2.2.md)
- [x] [2.3 Regresion existente de modulos criticos](2.3.md)

## Checklist De Avance De Fase

- [x] todas las tareas listadas en esta fase estan creadas
- [x] cada tarea tiene comandos o verificacion explicita
- [x] no hay tareas fuera del rango definido para la fase
- [x] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [x] Career y Subject en capas target
- [x] contratos externos sin ruptura
- [x] tests existentes de modulos criticos en verde
- [x] estado de fase actualizado en README de iteracion
