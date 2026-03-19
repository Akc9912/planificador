# Documentacion De Iteraciones

Este directorio contiene las iteraciones de documentacion para implementar la arquitectura y stack del backend de forma incremental y sin sobredimensionar.

## Iteracion Activa

- [iteracion-01-implementacion-arquitectura-stack-backend](iteracion-01-implementacion-arquitectura-stack-backend/README.md)

## Regla Operativa

- Una sola iteracion activa por vez.
- Trabajo modulo por modulo, de mayor criticidad a menor.
- Sin introducir tecnologias de niveles superiores sin presion medible.
- Sin crear tests nuevos en esta iteracion.

## Git Flow Setup

Se implementó **Git Flow** con 3 ambientes:

```
main (Producción)
  ↑ release/* + hotfix/*
  
staging (Pre-Producción)
  ↑ release/* + hotfix/*
  
develop (Desarrollo)
  ↑ feature/*, bugfix/*, chore/*
```

**Ramas de trabajo:**
- `feature/nombre-descriptivo` - Migraciones, features nuevas
- `bugfix/descripcion` - Correcciones menores
- `chore/tarea` - Docs, deps, config
- `release/iteracion-XX` - Preparación de release
- `hotfix/critica` - Emergencias en producción (solo de main)

**Ver estrategia completa:** [BRANCHING-STRATEGY.md](BRANCHING-STRATEGY.md)

Para la iteración 01, usar:
```bash
git checkout develop
git pull
git checkout -b feature/iteracion-01-fase-1
```
