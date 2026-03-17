# Fase 1 - Setup y Configuracion Modular

## Objetivo de fase

Dejar el backend con base tecnica reproducible: proyecto compilable, datasource definido, JWT validado, contratos modulares compartidos y testing minimo para impedir regresiones tempranas.

## Estado actual segun el plan

La fase figura en progreso en el plan fuente, pero el repo ya cumple los criterios reales de cierre de la base tecnica modular. Esta documentacion baja ese cierre a evidencia verificable.

## Estado real auditado del repo - 17/03/2026

- El proyecto compila con `./mvnw -q -DskipTests compile`.
- `application.properties` ya expone `spring.datasource.url`, `jwt.secret` y `supabase.jwt.secret`.
- Existen `shared/api`, `shared/event` y `ModuleBoundariesTest` como base real de aislamiento modular.
- La suite modular actual en `target/surefire-reports` esta en verde para arquitectura, auth y career.
- La parte de mapeo JPA, APIs basicas, testing y borde de alcance ya quedo cerrada para la base modular activa de Fase 1.

## Borde de alcance de esta fase

- Fase 1 valida base tecnica modular, no implementacion completa de todos los modulos del MVP.
- Subject y Equivalence completos se cierran en Fase 2.
- Si una tarea exige CRUD o logica completa de Subject/Equivalence para declararse cerrada, ese criterio esta fuera de alcance de Fase 1.

## Convencion de rutas congelada en Fase 1

- Se adopta como contrato vigente la convencion exacta del codigo actual.
- Prefix de Career: `/careers`.
- Prefix de Auth modular: `/auth`.
- Endpoints de documentacion tecnica: `/v3/api-docs`, `/swagger-ui`, `/swagger-ui.html`.
- Cualquier cambio futuro de convencion debe hacerse en un corte transversal unico (codigo, seguridad, tests y docs).

## Patron oficial de autenticacion y ownership congelado en Fase 1

- El filtro JWT modular valida token y carga el contexto de seguridad con principal igual a userId en formato string UUID.
- En controladores de modulos activos, el userId autenticado se obtiene desde `SecurityContextHolder.getContext().getAuthentication().getName()` y se convierte a UUID.
- Si el valor no es UUID valido, se considera token invalido para reglas de negocio.
- Las operaciones con ownership no deben aceptar `userId` por path o query cuando la identidad ya viene del token.
- Decision de fase: no extraer helper compartido en Fase 1. Se mantiene el patron actual y el posible refactor de helper se difiere a Fase 2 para evitar cambios innecesarios en cierre de base tecnica.

## Tareas de la fase

- [x] [1.1 Crear Proyecto Spring Boot](./1.1.md)
- [x] [1.2 Configurar Conexion a Base de Datos](./1.2.md)
- [x] [1.3 Mapear Entidades JPA](./1.3.md)
- [x] [1.4 Configurar Validacion JWT Supabase](./1.4.md)
- [x] [1.5 APIs REST Basicas](./1.5.md)
- [x] [1.6 Comunicacion Entre Modulos](./1.6.md)
- [x] [1.7 Testing](./1.7.md)

## Checklist de avance

- [x] 1.1 Base de proyecto validada y compilando.
- [x] 1.2 Configuracion PostgreSQL alineada a entorno.
- [x] 1.3 Base JPA demostrada sobre schema objetivo para el backend modular activo.
- [x] 1.4 Seguridad JWT modular validada contra Supabase.
- [x] 1.5 Contrato REST base autenticado y con ownership, validado para modulos activos.
- [x] Convencion de rutas de Fase 1 alineada al codigo real.
- [x] Patron oficial de autenticacion y ownership documentado y congelado para Fase 1.
- [x] 1.6 Shared API y eventos usados sin imports directos entre modulos.
- [x] 1.7 Suite modular minima ejecutada y documentada.

## Bloque de cierre auditado - 17/03/2026

- Build baseline validado con `./mvnw -q -DskipTests compile`.
- Suite modular minima validada con `./mvnw -q -DskipTests=false -Dtest=ModuleBoundariesTest,CareerServiceTest,CareerControllerTest,AuthSessionServiceTest,AuthJwtAuthenticationFilterTest,AuthModuleControllerTest,SecurityConfigAuthIntegrationTest test`.
- Baseline actual de Fase 1: `50` pruebas verdes (`2` de arquitectura, `22` de auth y `26` de career), `0 errors`, `0 failures`.
- Reportes de respaldo en `target/surefire-reports`:
	- [ModuleBoundariesTest](../../../target/surefire-reports/TEST-aktech.planificador.architecture.ModuleBoundariesTest.xml)
	- [CareerServiceTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.career.service.CareerServiceTest.xml)
	- [CareerControllerTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.career.controller.CareerControllerTest.xml)
	- [AuthSessionServiceTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.auth.service.AuthSessionServiceTest.xml)
	- [AuthJwtAuthenticationFilterTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.auth.filter.AuthJwtAuthenticationFilterTest.xml)
	- [AuthModuleControllerTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.auth.controller.AuthModuleControllerTest.xml)
	- [SecurityConfigAuthIntegrationTest](../../../target/surefire-reports/TEST-aktech.planificador.modules.auth.config.SecurityConfigAuthIntegrationTest.xml)
- No se agregaron pruebas nuevas al legacy para justificar el cierre de fase.

## Criterio de salida de la fase

- El proyecto compila con `./mvnw -q -DskipTests compile`.
- La suite modular minima corre en verde sin depender de clases legacy nuevas.
- El codigo nuevo se comunica por `shared/api` y `shared/event`.
- La autenticacion JWT contra Supabase queda estable.
- La base tecnica y el contrato REST base quedan documentados y verificados para modulos activos.
- Subject y Equivalence completos quedan explicitamente fuera de este cierre y pasan a Fase 2.

## Estado de salida de la fase

Fase 1 cerrada (17/03/2026).

- Las tareas 1.1 a 1.7 quedan cerradas para el alcance real de la fase.
- Las definiciones base de rutas, autenticacion, ownership, JPA, errores y testing ya no deben reabrirse al entrar en Fase 2.
- El siguiente frente tecnico es verificar si queda algun borde menor de Career como modulo plantilla; despejado eso, abrir Subject y luego Equivalence.
