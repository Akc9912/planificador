# Baseline Capturado - Tarea 1.1

## Fecha: 2026-03-19

### Evidencia Recolectada

#### 1. Estructura de Módulos Actual
```
Módulos encontrados:
- Career (controller, service, model, repository, dto, enums)
- Subject (controller, service, model, repository, dto, enums, listener)
- Equivalence (controller, service, model, repository, dto, enums)
- Auth (controller, service, model, repository, dto, filter)
```
**Archivo**: modulos-estructura-actual.txt (30 líneas)

#### 2. Contratos y Eventos Compartidos
```
APIs compartidas:
- CareerApi.java
- SubjectApi.java

DTOs compartidos:
- CareerBasicDto.java
- SubjectBasicDto.java

Eventos de dominio:
- CareerDeletedEvent.java
- SubjectStatusChangedEvent.java

Excepciones globales:
- BusinessException.java
- NotFoundException.java
- GlobalExceptionHandler.java

Utilidades:
- ValidationUtils.java
```
**Archivo**: shared-contratos-eventos.txt (10 líneas)

#### 3. Resultados de Pruebas de Baseline

✅ **Compilación de Baseline**: EXITOSA
- Comando: `./mvnw clean compile -q`
- Resultado: Sin errores
- Archivo: compile-baseline.log

✅ **ModuleBoundariesTest**: EN VERDE
- Comando: `./mvnw -DskipTests=false -Dtest=ModuleBoundariesTest test -q`
- Resultado: Todos los límites de módulo validados
- Archivo: module-boundaries-baseline.log

### Estado de Baseline

- ✅ Fotografía técnica capturada
- ✅ Estructura de módulos documentada
- ✅ Contratos y eventos identificados
- ✅ Compilación baseline en verde
- ✅ Boundaries test baseline en verde

### Archivos Generados

- `docs/iteracion-01-implementacion-arquitectura-stack-backend/evidencias/fase-1/modulos-estructura-actual.txt`
- `docs/iteracion-01-implementacion-arquitectura-stack-backend/evidencias/fase-1/shared-contratos-eventos.txt`
- `docs/iteracion-01-implementacion-arquitectura-stack-backend/evidencias/fase-1/compile-baseline.log`
- `docs/iteracion-01-implementacion-arquitectura-stack-backend/evidencias/fase-1/module-boundaries-baseline.log`
- `docs/iteracion-01-implementacion-arquitectura-stack-backend/evidencias/fase-1/tests-baseline.log`

### Siguiente Paso

Se puede proceder a la tarea 1.2 (Estandarizar Java 21).

---

## Verificación de Cierre

- [x] Objetivo cumplido: Baseline técnico congelado
- [x] Verificación ejecutada: Tests y compilación en verde
- [x] Evidencia registrada: Archivos en fase-1/evidencias
- [x] Documentación actualizada: Este resumen
