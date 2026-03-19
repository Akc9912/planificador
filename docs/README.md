# Documentacion De Iteraciones

Este directorio contiene las iteraciones de documentacion para implementar la arquitectura y stack del backend de forma incremental y sin sobredimensionar.

## Iteracion Activa

- [iteracion-02-hardening-arquitectonico-backend](iteracion-02-hardening-arquitectonico-backend/README.md)

## Iteraciones Planificadas

- [iteracion-03-rate-limit-redis-seguridad-basica](iteracion-03-rate-limit-redis-seguridad-basica/README.md)
- [iteracion-04-cobertura-tablas-db-restantes](iteracion-04-cobertura-tablas-db-restantes/README.md)

## Iteraciones Cerradas

- [iteracion-01-implementacion-arquitectura-stack-backend](iteracion-01-implementacion-arquitectura-stack-backend/README.md)

## Regla Operativa

- Una sola iteracion activa por vez.
- Trabajo modulo por modulo, de mayor criticidad a menor.
- Sin introducir tecnologias de niveles superiores sin presion medible.
- Crear tests nuevos solo cuando agreguen cobertura de reglas/seguridad o prevengan regresiones del alcance.

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

Para la iteracion 02, usar:
```bash
git checkout develop
git pull
git checkout -b feature/iteracion-02-fase-1
```
