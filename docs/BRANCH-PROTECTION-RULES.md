# Reglas de Protección de Ramas - Branch Protection Rules

## Propósito

Definir reglas de protección en GitHub para garantizar que las ramas `main`, `staging` y `develop` mantengan integridad y tracibilidad.

---

## Configuración Recomendada

### 1. Rama `main` (Producción) - Máxima Protección

**Ubicación en GitHub:**
Settings → Branches → Branch protection rules → Add rule

**Pattern:**

```
main
```

**Reglas a habilitar:**

| Regla                                                | Configuración                | Justificación                             |
| ---------------------------------------------------- | ---------------------------- | ----------------------------------------- |
| **Require pull request reviews before merging**      | Sí, 1 aprobación             | Evita merges directos a producción        |
| **Require approval from code owners**                | Sí (si existe CODEOWNERS)    | Garantiza revisión de propietario         |
| **Require status checks to pass before merging**     | Sí                           | Ensures CI/CD green                       |
| **Require branches to be up to date before merging** | Sí                           | Evita conflictos no probados              |
| **Require conversation resolution before merging**   | Sí                           | Requiere resolver todos los comentarios   |
| **Require signed commits**                           | No (opcional)                | Extra security si tu equipo usa GPG       |
| **Require deployment reviews**                       | No (a revisar después)       | Si integras deployment automation         |
| **Require a pull request from a specific branch**    | Sí: `release/*` y `hotfix/*` | Solo release/hotfix pueden mergear a main |
| **Dismiss stale pull request approvals**             | Sí                           | Invalida approval si hay nuevos pushes    |
| **Restrict who can push to matching branches**       | Sí: Solo `@Administradores`  | Controla quién puede pushear directo      |

**Indicaciones a ignorar:**

- Bypass list: vacío
- Allow force pushes: NO
- Allow deletions: NO

---

### 2. Rama `staging` (Pre-Producción) - Alta Protección

**Pattern:**

```
staging
```

**Reglas a habilitar:**

| Regla                                                | Configuración                | Justificación                    |
| ---------------------------------------------------- | ---------------------------- | -------------------------------- |
| **Require pull request reviews before merging**      | Sí, 1 aprobación             | QA/testing antes de subir a prod |
| **Require approval from code owners**                | Sí (si existe CODEOWNERS)    | Revisión de propietario          |
| **Require status checks to pass before merging**     | Sí                           | CI/CD debe estar verde           |
| **Require branches to be up to date before merging** | Sí                           | Evita conflictos                 |
| **Require conversation resolution before merging**   | Sí                           | Resolver comentarios             |
| **Require a pull request from a specific branch**    | Sí: `release/*` e `hotfix/*` | Solo releases y hotfixes         |
| **Dismiss stale pull request approvals**             | Sí                           | Revalidar si hay nuevos pushes   |
| **Restrict who can push to matching branches**       | Sí: Solo `@Administradores`  | Control de acceso                |

**Indicaciones a ignorar:**

- Allow force pushes: NO
- Allow deletions: NO

---

### 3. Rama `develop` (Desarrollo) - Protección Normal

**Pattern:**

```
develop
```

**Reglas a habilitar:**

| Regla                                                | Configuración     | Justificación                       |
| ---------------------------------------------------- | ----------------- | ----------------------------------- |
| **Require pull request reviews before merging**      | Sí, 1 aprobación  | Garantiza revisión de código        |
| **Require approval from code owners**                | No (más relajado) | Desarrollo es más ágil              |
| **Require status checks to pass before merging**     | Sí                | CI/CD debe pasar                    |
| **Require branches to be up to date before merging** | Sí                | Evita conflictos                    |
| **Require conversation resolution before merging**   | No (más relajado) | Desarrollo puede cerrar comentarios |
| **Require a pull request from a specific branch**    | Opcional          | `feature/*`, `bugfix/*`, `chore/*`  |
| **Dismiss stale pull request approvals**             | No                | Más flexible en desarrollo          |
| **Restrict who can push to matching branches**       | No                | Más personas pueden colaborar       |

**Indicaciones a ignorar:**

- Allow force pushes: NO
- Allow deletions: NO

---

## Pasos para Configurar en GitHub

### Prerequisito: Crear archivo CODEOWNERS (Opcional pero Recomendado)

```bash
# En la raíz del proyecto o en .github/
# Crear archivo: .github/CODEOWNERS
```

**Contenido:**

```
# Backend architecture
src/main/java/aktech/planificador/modules/career/         @tu-usuario
src/main/java/aktech/planificador/modules/subject/        @tu-usuario
src/main/java/aktech/planificador/modules/equivalence/    @tu-usuario
src/main/java/aktech/planificador/modules/auth/           @tu-usuario
src/main/java/aktech/planificador/shared/                 @tu-usuario

# Docs
docs/                                                       @tu-usuario
ARCHITECTURE.md                                             @tu-usuario

# Default (el que no aparece arriba)
*                                                           @tu-usuario
```

Luego commitear:

```bash
git add .github/CODEOWNERS
git commit -m "chore: add CODEOWNERS file"
git push origin develop
```

### Configurar main

1. En GitHub: Settings → Branches
2. Click "Add rule"
3. Branch name pattern: `main`
4. Habilitar:
   - ✅ Require a pull request before merging
   - ✅ Require approvals (1)
   - ✅ Require code owner reviews (si existe CODEOWNERS)
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Require conversation resolution
   - ✅ Dismiss stale PR approvals
   - ✅ Restrict who can push (Administradores)
5. Click "Create"

### Configurar staging

Repetir el proceso anterior pero con pattern `staging` y con menos restricciones (sin code owners obligatorio).

### Configurar develop

Repetir pero con protección más normal (solo PR + 1 aprobación + CI).

---

## Validación de Status Checks (CI/CD)

Para que **"Require status checks to pass"** funcione, necesitas que tus workflows de CI publiquen status checks.

**En tu `.github/workflows/modular-quality-gates.yml`:**

Asegurate que existan:

```yaml
jobs:
  compile:
    # ...

  boundaries:
    # ...

  tests:
    # ...
```

Cada job auto-genera un status check que GitHub respetará en las branch protection rules.

Luego en Settings → Branches → `main` / `staging` / `develop`:

- Bajo "Require status checks to pass before merging"
- Selecciona los checks que deben estar ✅:
  - compile
  - boundaries
  - tests (modular-quality-gates / tests)

---

## Restricción por Patrón (Importante)

La regla **"Require a pull request from a specific branch"** restringe de dónde pueden venir PRs:

**Para `main`:**

```
release/*
hotfix/*
```

**Para `staging`:**

```
release/*
hotfix/*
```

**Para `develop`:**

```
feature/*
bugfix/*
chore/*
```

---

## Excepciones Temporales

En casos de emergencia, un administrador puede temporalmente:

1. Desactivar reglas (no recomendado)
2. Hacer force push (extremadamente raro)
3. Mergear sin PR (solo en hotfix crítico)

**Proceso obligatorio después:**

- Documentar por qué se usó
- Reactivar reglas inmediatamente
- Notificar al equipo

---

## Checklist de Configuración

- [ ] Crear `.github/CODEOWNERS` (opcional)
- [ ] Configurar reglas en `main`
- [ ] Configurar reglas en `staging`
- [ ] Configurar reglas en `develop`
- [ ] Verificar que CI/CD workflows están corriendo
- [ ] Probar PR bloqueada por falta de aprobación
- [ ] Probar PR bloqueada por CI en rojo
- [ ] Documentar excepciones en el proyecto

---

## Referencias

- GitHub Branch Protection: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches
- CODEOWNERS: https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners
