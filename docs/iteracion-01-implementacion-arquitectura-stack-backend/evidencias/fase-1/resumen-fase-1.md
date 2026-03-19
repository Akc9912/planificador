# Resumen Unico De Evidencias - Fase 1

## Contexto

Fase 1 de la iteracion `iteracion-01-implementacion-arquitectura-stack-backend`.
Objetivo: baseline tecnico + estandarizacion Java 21 + Docker operativo.
Fecha de consolidacion: 2026-03-19.

## Resultado Ejecutivo

- Baseline de arquitectura y contratos: COMPLETADO.
- Java 21 en proyecto y CI: COMPLETADO.
- Docker backend operativo: COMPLETADO.
- Smoke test OpenAPI: HTTP 200 en `/v3/api-docs`.

## Evidencia Consolidada

### 1) Baseline Arquitectura (Tarea 1.1)

#### Estructura de modulos detectada

- Modulos: `career`, `subject`, `equivalence`, `auth`.
- Estructura observada por modulo: `controller`, `service`, `repository`, `model`, `dto` (+ variaciones por modulo).

Detalle consolidado:

- `auth`: controller, dto, filter, model, repository, service
- `career`: controller, dto, enums, model, repository, service
- `equivalence`: controller, dto, enums, model, repository, service
- `subject`: controller, dto, enums, listener, model, repository, service

#### Contratos y eventos compartidos

APIs:

- `CareerApi`
- `SubjectApi`

DTOs:

- `CareerBasicDto`
- `SubjectBasicDto`

Eventos:

- `CareerDeletedEvent`
- `SubjectStatusChangedEvent`

Excepciones/util:

- `BusinessException`
- `NotFoundException`
- `GlobalExceptionHandler`
- `ValidationUtils`

#### Validacion baseline

- Compilacion baseline ejecutada: OK.
- `ModuleBoundariesTest` ejecutado: OK.
- Suite baseline registrada en ejecucion previa: BUILD SUCCESS.

Nota: en logs aparece warning de `sun.misc.Unsafe` de libreria externa (Guice/Maven runtime), sin falla de build.

### 2) Estandarizacion Java 21 (Tarea 1.2)

Cambios aplicados:

- `pom.xml`: `java.version` de `24` a `21`.
- `.github/workflows/modular-quality-gates.yml`: `java-version` de `"24"` a `"21"`.

Ajuste tecnico adicional necesario durante validacion Docker:

- `pom.xml`: agregado `maven.compiler.release=${java.version}` para garantizar bytecode Java 21 y compatibilidad con JRE 21 en contenedor.

Validacion:

- Compilacion post-cambio: OK.
- Proyecto y workflow CI alineados a Java 21.

Observacion de entorno local:

- Runtime local detectado en ejecucion: Java 24.0.2.
- Esto no bloquea el objetivo mientras el artefacto compile con `release 21`.

### 3) Docker Backend Operativo (Tarea 1.3)

Implementacion:

- Se creo `Dockerfile` con base `eclipse-temurin:21-jre`.
- Se empaqueto JAR y se construyo imagen `planificador-backend:local`.
- Se levanto Postgres auxiliar en contenedor para smoke test local.
- Se levanto backend en contenedor sobre puerto `8081`.

Resultado smoke test:

- Endpoint validado: `GET /v3/api-docs`
- Respuesta final: HTTP `200`
- `Content-Type`: `application/json`

Incidencias reales y correcciones:

1. Docker daemon no disponible al inicio.

- Causa: Docker Desktop no iniciado.
- Resolucion: iniciar daemon y reintentar.

2. Falla de runtime por version de bytecode.

- Error observado: `UnsupportedClassVersionError` (class file version 68 vs runtime 65).
- Causa: clases compiladas con Java 24.
- Resolucion: agregar `maven.compiler.release=${java.version}` con `java.version=21` y recompilar.

3. Error 500 en `/v3/api-docs`.

- Causa: incompatibilidad `springdoc-openapi-starter-webmvc-ui` 2.6.0 con stack actual.
- Resolucion: actualizar dependency a `2.8.6`.

## Checklist De Cierre Fase 1

- [x] Baseline tecnico documentado.
- [x] Java 21 estandarizado en proyecto y CI.
- [x] Docker backend operativo.
- [x] Evidencia consolidada en un unico archivo.

## Archivos Fuente Consolidados

Este resumen reemplaza y consolida la informacion que estaba distribuida en multiples logs/txt/md de `evidencias/fase-1`.

## Estado Final

FASE 1: CERRADA.
