# Estrategia de Branching - Git Flow

## Resumen

Este proyecto sigue **Git Flow** con 3 ambientes:
- **Production**: main
- **Pre-Production**: staging
- **Development**: develop

## Estructura de Ramas

### Ramas Principales (Long-lived)

| Rama | Propósito | Entorno | Protección | Pull Requests |
|------|----------|---------|-----------|---------------|
| `main` | Código en producción | Production | Sí (require review) | Solo release/* y hotfix/* |
| `staging` | Pre-producción y testing | Pre-Prod | Sí (require review) | Solo release/* e hotfix/* |
| `develop` | Integración de desarrollo | Development | Sí (require review) | feature/*, bugfix/*, chore/* |

### Ramas de Trabajo (Short-lived)

| Tipo | Patrón | Origen | Destino | Propósito |
|------|--------|--------|---------|----------|
| Feature | `feature/*` | develop | develop | Nuevas funcionalidades o migraciones |
| Bugfix | `bugfix/*` | develop | develop | Correcciones de bugs no críticos |
| Chore | `chore/*` | develop | develop | Tareas, actualizaciones, documentación |
| Release | `release/*` | develop | staging + main | Preparación de versión para producción |
| Hotfix | `hotfix/*` | main | main + develop | Correcciones críticas en producción |

## Flujo de Trabajo Estándar

### 1. Trabajar en una Feature (Migración, Feature Nueva, etc.)

```bash
# Asegurar que develop está actualizado
git checkout develop
git pull origin develop

# Crear rama de feature desde develop
git checkout -b feature/nombre-descriptivo

# Trabajar con commits semánticos (conventional commits)
git commit -m "feat: descripción del cambio"
git commit -m "refactor: reorganizar estructura"
git commit -m "docs: actualizar documentación"

# Pushear la rama
git push -u origin feature/nombre-descriptivo
```

**Commits semánticos recomendados:**
- `feat:` - Nueva funcionalidad
- `refactor:` - Refactorización sin cambio funcional
- `fix:` - Corrección de bug
- `docs:` - Cambios en documentación
- `test:` - Cambios en tests
- `chore:` - Cambios en deps, build, config
- `perf:` - Mejora de performance

### 2. Crear Pull Request → develop

Cuando la feature esté lista:

```bash
# Desde GitHub o terminal:
# git push origin feature/nombre-descriptivo
```

**Checklist para PR:**
- [ ] Tests existentes en verde
- [ ] ModuleBoundariesTest validado
- [ ] Documentación iteración/fase actualizada
- [ ] Sin imports prohibidos
- [ ] Sin nuevas dependencias de nivel 1+

### 3. Feature Completada → develop

Una vez aprobado el PR, la feature se mergea a `develop`.

### 4. Cierre de Iteración/Fase → release

Cuando una iteración (o fase importante) está lista:

```bash
# Crear rama de release desde develop
git checkout develop
git pull
git checkout -b release/iteracion-01-fase-1

# (Opcional) bump de versión en pom.xml y otros archivos
# Commit final
git commit -m "chore: release iteracion-01-fase-1"
git push -u origin release/iteracion-01-fase-1
```

### 5. Release → staging (QA)

```bash
# PR: release/iteracion-01-fase-1 → staging
# Assign a reviewer / QA
# Once approved, merge con "Create a merge commit"
git checkout staging
git pull
git merge --no-ff release/iteracion-01-fase-1
git push origin staging
```

### 6. Release → main (Producción)

```bash
# PR: staging → main (o direct merge si automatizado)
# Tag la versión
git checkout main
git pull
git merge --no-ff staging
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags
```

Luego mergear release de vuelta a develop:

```bash
git checkout develop
git pull
git merge --no-ff release/iteracion-01-fase-1
git push origin develop
```

Finalmente, borrar la rama release:

```bash
git branch -d release/iteracion-01-fase-1
git push origin --delete release/iteracion-01-fase-1
```

## Flujo de Bugfix/Chore

Para correcciones rápidas o tareas menores:

```bash
# Desde develop
git checkout develop
git pull
git checkout -b bugfix/descripcion-bug

# o

git checkout -b chore/descripcion-tarea

# Trabajar y commitear
git commit -m "fix: descripcion"
git push -u origin bugfix/descripcion-bug

# PR a develop
```

## Flujo de Hotfix (Emergencia en Producción)

```bash
# Crear hotfix desde main
git checkout main
git pull
git checkout -b hotfix/descripcion-critico

# Corregir y commitear
git commit -m "fix: descripcion realmente critica"
git push -u origin hotfix/descripcion-critico

# PR a main (y después mergear de vuelta a develop)
# Merge a main → tag versión patch (v1.0.1)
# Merge tag a develop también
```

## Convenciones de Nombres

### Feature Branch Naming

✅ **Bueno:**
- `feature/migracion-career-layer`
- `feature/java21-upgrade`
- `feature/docker-backend-setup`
- `feature/auth-supabase-integration`

❌ **Evitar:**
- `feature/work`
- `feature/fix`
- `feature/test`

### Release Branch Naming

✅ **Bueno:**
- `release/iteracion-01`
- `release/v1.0.0`
- `release/fase-1-baseline`

### Hotfix Branch Naming

✅ **Bueno:**
- `hotfix/security-token-vuln`
- `hotfix/database-connection-leak`

## Recomendaciones

1. **Siempre mergear con `--no-ff`** en ramas principales (develop, staging, main) para mantener historial claro:
   ```bash
   git merge --no-ff feature/nombre
   ```

2. **Rebase en lugar de merge** para ramas de feature si trabajan varias personas:
   ```bash
   git rebase develop
   git push -f origin feature/nombre (solo si es tu rama personal)
   ```

3. **Revisar PRs antes de mergear** - nunca mergear código propio sin revisión.

4. **Borrar ramas completadas** para mantener el repositorio limpio:
   ```bash
   git branch -d feature/nombre
   git push origin --delete feature/nombre
   ```

5. **Sincronizar periódicamente** si trabajas en una feature larga:
   ```bash
   git fetch origin
   git rebase origin/develop
   ```

## Estado Actual

Ramas configuradas (19/03/2026):

```
* staging → origin/staging (Pre-Prod)
  develop → origin/develop (Development)
  main → origin/main (Production)
```

## Referencias

- Git Flow en profundidad: https://nvie.com/posts/a-successful-git-branching-model/
- Conventional Commits: https://www.conventionalcommits.org/
