# Fase 1 - Setup y Configuracion Modular

## Objetivo de fase

Dejar el backend con base tecnica reproducible: proyecto compilable, datasource definido, JWT validado, contratos modulares compartidos y testing minimo para impedir regresiones tempranas.

## Estado actual segun el plan

La fase figura en progreso. El repo ya tiene avances concretos en `shared`, `auth`, `career`, seguridad y guardrails modulares. Esta documentacion baja el plan a pasos operativos cerrables.

## Tareas de la fase

- [ ] [1.1 Crear Proyecto Spring Boot](./1.1.md)
- [ ] [1.2 Configurar Conexion a Base de Datos](./1.2.md)
- [ ] [1.3 Mapear Entidades JPA](./1.3.md)
- [ ] [1.4 Configurar Validacion JWT Supabase](./1.4.md)
- [ ] [1.5 APIs REST Basicas](./1.5.md)
- [ ] [1.6 Comunicacion Entre Modulos](./1.6.md)
- [ ] [1.7 Testing](./1.7.md)

## Checklist de avance

- [ ] 1.1 Base de proyecto validada y compilando.
- [ ] 1.2 Configuracion PostgreSQL alineada a entorno.
- [ ] 1.3 Entidades nuevas modeladas sobre UUID y reglas de schema objetivo.
- [ ] 1.4 Seguridad JWT modular validada contra Supabase.
- [ ] 1.5 Endpoints base autenticados y con ownership.
- [ ] 1.6 Shared API y eventos usados sin imports directos entre modulos.
- [ ] 1.7 Suite modular minima ejecutada y documentada.

## Criterio de salida de la fase

- El proyecto compila con `./mvnw -q -DskipTests compile`.
- La suite modular minima corre en verde sin depender de clases legacy nuevas.
- El codigo nuevo se comunica por `shared/api` y `shared/event`.
- La autenticacion JWT contra Supabase queda estable.
- La base tecnica permite seguir con cierre de Career y luego Subject/Equivalence.
