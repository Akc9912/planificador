<div align="center">

# 📋 Plan de Migración - miCarrera Planner Backend

### Desarrollo de MVP Backend Spring Boot

**🎯 Progreso de Desarrollo: 55% Completado (estimado)**

```
██████████████████████░░░░░░░░░░░░░░░░░░ 55%
```

**Estado:** Backend de prueba existente en adaptacion (no conectado a produccion)  
**Siguiente:** Cerrar setup modular de Fase 1 en Career (tests + validacion modular + hardening sobre UUID). Frontend se adapta cuando toque su migracion.

</div>

---

## 📋 Tabla de Contenidos

- [🎯 Contexto y Objetivo](#-contexto-y-objetivo)
- [📊 Módulos del Sistema](#-módulos-del-sistema)
- [📈 Estado de Desarrollo](#-estado-de-desarrollo)
- [📦 Fase 1: Setup y Configuración Modular](#-fase-1-setup-y-configuración-modular)
- [🔧 Fase 2: MVP - Módulos Core](#-fase-2-mvp---módulos-core)
- [🚀 Fase 3: Módulos Complementarios](#-fase-3-módulos-complementarios)
- [🎯 Fase 4: Features Avanzadas y Optimización](#-fase-4-features-avanzadas-y-optimización)
- [🔌 Integración con Frontend](#-integración-con-frontend)
- [🔧 Arquitectura Modular](#-arquitectura-modular-no-necesariamente-microservicios)
- [⚡ Timeline y Prioridades](#-timeline-y-prioridades)

---

## 🎯 Contexto y Objetivo

### Situación

**Frontend Actual:** Monorepo Next.js + Supabase (en producción)  
**Backend:** Existe un prototipo Spring Boot legacy que se esta adaptando

### Objetivo

**Desarrollar MVP Backend Spring Boot** que:

- ✅ Provea APIs REST completas para el frontend
- ✅ Implemente lógica de negocio compleja (correlativas, validaciones, cálculos)
- ✅ Use Supabase Auth para autenticación (validación JWT)
- ✅ Arquitectura modular (buenas prácticas, no necesariamente microservicios)
- ✅ Testing robusto y documentación Swagger

### Flujo de Desarrollo

```
1. Backend MVP (este proyecto) → Completar 100%
                ↓
2. Frontend se integra → Consume las APIs del backend
                ↓
3. Migración gradual → Frontend deja Supabase directo
```

**📌 IMPORTANTE:** El frontend NO será migrado en este plan. Este documento se enfoca **exclusivamente en el desarrollo del backend MVP**.

---

## 📊 Módulos del Sistema

### Definición de Módulos Basada en Esquema DB

La arquitectura del backend se organizará en **9 módulos independientes**, cada uno responsable de un dominio específico del sistema:

<table>
  <tr>
    <th>#</th>
    <th>Módulo</th>
    <th>Tablas DB</th>
    <th>Responsabilidad</th>
    <th>Prioridad</th>
  </tr>
  <tr>
    <td>1</td>
    <td><b>Career Module</b></td>
    <td>careers</td>
    <td>Gestión de carreras universitarias</td>
    <td>🔥 MVP</td>
  </tr>
  <tr>
    <td>2</td>
    <td><b>Subject Module</b></td>
    <td>subjects, subject_modules, subject_schedules</td>
    <td>Gestión de materias, módulos evaluables y horarios</td>
    <td>🔥 MVP</td>
  </tr>
  <tr>
    <td>3</td>
    <td><b>Equivalence Module</b></td>
    <td>equivalences</td>
    <td>Equivalencias entre materias</td>
    <td>🔥 MVP</td>
  </tr>
  <tr>
    <td>4</td>
    <td><b>Auth Module</b></td>
    <td>auth.users (Supabase)</td>
    <td>Validación JWT y autorización</td>
    <td>🔥 MVP</td>
  </tr>
  <tr>
    <td>5</td>
    <td><b>Event Module</b></td>
    <td>events, event_schedules, event_subjects</td>
    <td>Gestión de eventos (exámenes, entregas)</td>
    <td>🟡 Post-MVP</td>
  </tr>
  <tr>
    <td>6</td>
    <td><b>Reminder Module</b></td>
    <td>reminders, reminder_subjects</td>
    <td>Sistema de recordatorios</td>
    <td>🟡 Post-MVP</td>
  </tr>
  <tr>
    <td>7</td>
    <td><b>UserSettings Module</b></td>
    <td>user_settings</td>
    <td>Configuración personalizada del usuario</td>
    <td>🟡 Post-MVP</td>
  </tr>
  <tr>
    <td>8</td>
    <td><b>Audit Module</b></td>
    <td>audit_logs</td>
    <td>Sistema de auditoría y trazabilidad</td>
    <td>� Post-MVP</td>
  </tr>
  <tr>
    <td>9</td>
    <td><b>Support Module</b></td>
    <td>support_tickets</td>
    <td>Sistema de tickets de soporte</td>
    <td>🟢 Futuro</td>
  </tr>
</table>

### Estructura de Cada Módulo

Cada módulo sigue la misma estructura de capas:

```
modules/{module_name}/
├── controller/     → REST Controllers (@RestController)
├── service/        → Business Logic (@Service)
├── repository/     → Data Access (@Repository)
├── model/          → JPA Entities (@Entity)
├── dto/            → Data Transfer Objects
│   ├── request/
│   └── response/
└── exception/      → Excepciones específicas del módulo
```

### Comunicación Entre Módulos

**Regla fundamental:** Los módulos NO deben importarse directamente.

**Mecanismos permitidos:**

1. **Interfaces en `shared/api/`** - Para consultas sincrónicas
2. **Eventos en `shared/event/`** - Para comunicación asíncrona
3. **DTOs en `shared/dto/`** - Para transferencia de datos

Ver [ARCHITECTURE.md](ARCHITECTURE.md) para detalles completos.

---

## 📈 Estado de Desarrollo

### Snapshot auditado (14/03/2026)

| Area                      | Estado actual                 | Evidencia                                                        |
| ------------------------- | ----------------------------- | ---------------------------------------------------------------- |
| Build                     | FUNCIONAL                     | `./mvnw -DskipTests compile` en verde (`BUILD SUCCESS`)          |
| API Auth                  | Modular funcional             | `modules/auth` (`AuthModuleController`, `AuthSessionService`, `AuthJwtAuthenticationFilter`) |
| API Usuarios              | Funcional sobre modelo legacy | `UsuarioController` + `UsuarioService`                           |
| API Materias              | Funcional sobre modelo legacy | `MateriaController` + `MateriaService`                           |
| API Careers               | Funcional (MVP basico)        | `modules/career` con entity/repository/service/controller/dto    |
| Setup modular shared      | Parcial funcional             | `shared/api`, `shared/event`, `shared/util`, `shared/exception`  |
| API Eventos               | Desacoplada del MVP           | Endpoints legacy responden `410 GONE` (Event Module Post-MVP)    |
| API Recordatorios         | Desacoplada del MVP           | Endpoints legacy responden `410 GONE` (Reminder Module Post-MVP) |
| Seguridad                 | Endurecida (modular)          | Filtro JWT modular + reglas por rol activas en `SecurityConfig`  |
| Integracion Supabase Auth | Funcional en backend          | Validacion JWT activa; login/register/change-password delegados a Supabase (`410 GONE`) |
| Persistencia              | En transicion a PostgreSQL    | Driver/URL PostgreSQL listos, con entidades legacy aun activas   |
| Testing                   | Parcial funcional             | Suite auth modular en verde (`26/26`) + `PlanificadorApplicationTests#contextLoads` |

### Lo que si esta avanzado

- Documentacion base (`README.md`, `MIGRATION_PLAN.md`, `ARCHITECTURE.md`).
- Configuracion PostgreSQL aplicada (`pom.xml` + `application.properties`).
- Script objetivo unificado en `humanis_db_init.sql` (init limpio con tablas, indices, triggers y RLS).
- Build en verde con `./mvnw -DskipTests compile`.
- Endpoints legacy de Event/Reminder desacoplados del MVP (`410 GONE`).
- Capa de seguridad endurecida: filtro JWT unificado y reglas por rol en `SecurityConfig`.
- Modulo `auth` migrado a implementacion modular independiente (`modules/auth`) sin dependencia de codigo legacy.
- Hardening en `CareerController`: ownership por `userId` tomado desde SecurityContext.
- Swagger/OpenAPI habilitado (`/swagger-ui.html` y `/v3/api-docs`).
- Suite de tests de auth modular en verde (`26/26`): servicio de sesion, filtro JWT, controlador y reglas de seguridad.
- Guardrail de arquitectura automatizado en CI con `.github/workflows/modular-quality-gates.yml` (`ModuleBoundariesTest` + suite modular Auth/Career).
- CRUD base de usuarios y materias para backend legacy.
- Modulo `career` implementado en capas con DTOs y endpoints CRUD/ownership.
- Base de comunicacion modular creada (`shared/api`, `shared/event`, `shared/util`, `shared/exception`).
- Integracion inicial entre modulos (`CareerService` implementa `CareerApi` y publica `CareerDeletedEvent`).

### Bloqueadores actuales

1. Brecha entre esquema objetivo UUID (`humanis_db_init.sql`) y entidades actuales con `Integer`.
2. Cobertura de tests aun baja para modulo Career y rutas legacy criticas.
3. Definir roadmap de reintroduccion de Event/Reminder como modulos Post-MVP.
4. Preparar handoff de contratos API (`Career` + `Auth`) para integracion frontend cuando corresponda.
5. Extender checks CI modulares a medida que entren Subject/Equivalence, manteniendo la regla de no acoplamiento.

**Nota de alcance:** la adaptacion de frontend a contratos backend se ejecuta cuando inicie la migracion del frontend; no bloquea el cierre tecnico de Career.

### Prioridades inmediatas

1. Agregar tests de servicios y controladores para flujos criticos de Career.
2. Mantener build en verde con control de regresiones.
3. Preparar handoff del contrato API de Career/Auth para consumir desde frontend cuando toque su migracion.
4. Planificar el inicio de Subject/Equivalence sobre esquema UUID una vez cerrado Career.
5. Expandir el workflow de calidad modular para nuevos modulos (sumar Subject/Equivalence al migrarlos).

### Decisión de Base de Datos

**Opción Elegida: Supabase PostgreSQL** ✅

- ✅ Datos ya están en producción
- ✅ Sin migración de datos
- ✅ Supabase Auth integrado
- ✅ Rollback sencillo si es necesario
- ⚠️ Dependencia de Supabase (aceptable a corto/medio plazo)

---

## 📦 Fase 1: Setup y Configuración Modular

> **Timeline:** 1-2 semanas  
> **Progreso:** 0% → 15%  
> **Objetivo:** Proyecto Spring Boot configurado con arquitectura modular y autenticación funcionando

### 1.1 Crear Proyecto Spring Boot

**Tecnologías necesarias:**

- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL Driver
- Spring Security
- Spring Web

**Arquitectura: Modular por Dominio**

El proyecto se organizará por módulos de dominio para mejor organización, mantenibilidad y testeo:

```
src/main/java/aktech/planificador/
├── config/              → Configuración global (Security, JWT, DB)
├── shared/              → Código compartido (exceptions, utils, DTOs base)
└── modules/             → Módulos de dominio (cada uno con sus capas)
    ├── career/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    ├── subject/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    ├── auth/
    └── equivalence/
```

**Ventajas:**

- ✅ Cada módulo es independiente y cohesivo
- ✅ Fácil de testear por módulo
- ✅ Equipos pueden trabajar en paralelo
- ✅ Preparado para extraer a microservicio
- ✅ Escalabilidad y mantenibilidad

**Reglas de módulos:**

- Módulos NO deben importar código de otros módulos directamente
- Comunicación entre módulos vía interfaces o eventos
- Código compartido va a `/shared`
- NO circular dependencies

### 1.2 Configurar Conexión a Base de Datos

**Conectar a Supabase PostgreSQL existente:**

- Obtener connection string de Supabase
- Configurar datasource en application.properties
- Habilitar conexión directa a PostgreSQL (no RLS)

### 1.3 Mapear Entidades JPA

**Desafíos técnicos:**

#### UUID en lugar de Integer

```
⚠️ Supabase usa UUID, Spring Boot legacy usa Integer
✅ Solución: Usar @Id private UUID id
```

#### Enums PostgreSQL

```
⚠️ PostgreSQL tiene enums nativos
✅ Solución: @Enumerated(EnumType.STRING)
```

#### Arrays de PostgreSQL

```
⚠️ correlatives es TEXT[] en PostgreSQL
✅ Solución: @Type(JsonBinaryType.class) + List<UUID>
         o @JdbcTypeCode(SqlTypes.ARRAY)
```

#### Relación con auth.users

```
⚠️ auth.users es tabla de Supabase, no nuestra
✅ Solución: No mapear relación JPA, solo guardar user_id
```

**Entidades a crear:**

- Career (carrera)
- Subject (materia)
- SubjectModule (módulo)
- Equivalence (equivalencia)

### 1.4 Configurar Validación JWT Supabase

**Spring Security debe:**

- Validar JWT firmados por Supabase
- Extraer user_id del token
- No requerir auth.users en DB

**Configuración:**

- Obtener Supabase JWT Secret
- Configurar JWTDecoder con secret
- Crear filtro que valida cada request
- Extraer user_id y agregarlo a SecurityContext

### 1.5 APIs REST Básicas

**Implementar CRUD para cada entidad:**

- GET /api/careers (listar carreras del usuario)
- POST /api/careers (crear carrera)
- GET /api/subjects?careerIdcareer123 (listar materias de carrera)
- POST /api/subjects (crear materia)
- PUT /api/subjects/:id (actualizar materia)
- etc.

**Importante:**

- Todas las APIs requieren autenticación
- Validar ownership: usuario solo ve sus datos
- DTOs para request/response
- Manejo de errores consistente

### 1.6 Comunicación Entre Módulos

**Principio fundamental:** Módulos deben ser independientes pero poder colaborar.

#### ❌ Anti-patrón: Imports Directos

```java
// modules/subject/service/SubjectService.java
import aktech.planificador.modules.career.service.CareerService; // ❌ NO

public class SubjectService {
    @Autowired
    private CareerService careerService; // ❌ Acoplamiento directo
}
```

#### ✅ Patrón Recomendado: Interfaces en Shared

**Opción 1: Interfaces de Comunicación**

```java
// shared/api/CareerApi.java
public interface CareerApi {
    CareerResponseDto getCareer(UUID careerId);
    boolean existsCareer(UUID careerId);
    boolean userOwnsCareer(UUID userId, UUID careerId);
}

// modules/career/service/CareerService.java
@Service
public class CareerService implements CareerApi {
    // Implementación
}

// modules/subject/service/SubjectService.java
public class SubjectService {
    @Autowired
    private CareerApi careerApi; // ✅ Depende de interfaz, no implementación

    public void createSubject(SubjectRequestDto dto) {
        if (!careerApi.existsCareer(dto.getCareerId())) {
            throw new BusinessException("Career not found");
        }
        // continuar...
    }
}
```

**Opción 2: Eventos de Dominio**

```java
// shared/event/CareerDeletedEvent.java
@Getter
@AllArgsConstructor
public class CareerDeletedEvent {
    private UUID careerId;
    private UUID userId;
}

// modules/career/service/CareerService.java
@Service
public class CareerService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void deleteCareer(UUID id) {
        // ... eliminar carrera
        eventPublisher.publishEvent(new CareerDeletedEvent(id, userId));
    }
}

// modules/subject/listener/CareerEventListener.java
@Component
public class CareerEventListener {
    @Autowired
    private SubjectRepository subjectRepository;

    @EventListener
    public void handleCareerDeleted(CareerDeletedEvent event) {
        // Eliminar materias de la carrera
        subjectRepository.deleteByCareerId(event.getCareerId());
    }
}
```

**Ventajas de eventos:**

- ✅ Desacoplamiento total
- ✅ Fácil agregar nuevos listeners
- ✅ Preparado para event-driven architecture
- ✅ Asíncrono (con @Async si es necesario)

#### Estructura de Shared

```
shared/
├── api/                  → Interfaces de comunicación
│   ├── CareerApi.java
│   └── SubjectApi.java
├── event/               → Eventos de dominio
│   ├── CareerDeletedEvent.java
│   └── SubjectStatusChangedEvent.java
├── exception/           → Excepciones comunes
│   └── BusinessException.java
├── dto/                 → DTOs base
│   └── GenericResponseDto.java
└── util/                → Utilidades
    └── ValidationUtils.java
```

### 1.7 Testing

**Tests necesarios:**

- Unit tests de servicios (por módulo)
- Integration tests de repositorios
- Tests de validación JWT
- Tests de ownership
- Tests de comunicación entre módulos (events/interfaces)

**Testing por módulo:**

```
modules/career/
└── test/
    ├── controller/
    ├── service/
    └── repository/

modules/subject/
└── test/
    ├── controller/
    ├── service/
    └── repository/
```

**Criterio de éxito Fase 1:**

```
✅ Backend conectado a Supabase PostgreSQL
✅ Entidades JPA mapeadas correctamente
✅ JWT Supabase validado
✅ APIs CRUD funcionando
✅ Módulos independientes sin imports directos
✅ Comunicación entre módulos via interfaces/eventos
✅ Tests > 70% coverage
✅ Documentación Swagger generada
```

---

## 🔧 Fase 2: MVP - Módulos Core

> **Timeline:** 3-4 semanas  
> **Progreso:** 15% → 60%  
> **Objetivo:** Implementar los 4 módulos core con APIs REST completas y lógica de negocio

### Módulos a Implementar

#### 🔥 Prioridad 1: Career Module

**Funcionalidad:**

- CRUD completo de carreras
- Cálculo de progreso (materias aprobadas/total)
- Cálculo de horas/créditos acumulados
- Estadísticas por carrera

**APIs:**

```
GET    /api/careers
POST   /api/careers
GET    /api/careers/{id}
PUT    /api/careers/{id}
DELETE /api/careers/{id}
GET    /api/careers/{id}/stats
```

**Estimación:** 3-4 días

---

#### 🔥 Prioridad 2: Subject Module (El más complejo)

**Funcionalidad:**

- CRUD de materias
- CRUD de subject_modules (módulos evaluables)
- CRUD de subject_schedules (horarios semanales)
- Validación de correlativas (lógica de prerequisitos)
- Cálculo de promedio ponderado
- Identificación de materias disponibles vs bloqueadas
- Gestión de estados (pendiente, cursando, regular, aprobada, libre)

**APIs:**

```
GET    /api/subjects?careerId={uuid}
POST   /api/subjects
GET    /api/subjects/{id}
PUT    /api/subjects/{id}
DELETE /api/subjects/{id}
GET    /api/subjects/{id}/status          → incluye si está bloqueada
GET    /api/subjects/{id}/prerequisites   → lista correlativas
GET    /api/subjects/{id}/unlocks         → qué materias habilita
GET    /api/subjects/{id}/modules         → módulos evaluables
POST   /api/subjects/{id}/modules
PUT    /api/subjects/{id}/modules/{moduleId}
DELETE /api/subjects/{id}/modules/{moduleId}
GET    /api/subjects/{id}/schedules       → horarios semanales
POST   /api/subjects/{id}/schedules
DELETE /api/subjects/{id}/schedules/{scheduleId}
```

**Estimación:** 8-10 días

---

#### 🔥 Prioridad 3: Equivalence Module

**Funcionalidad:**

- CRUD de equivalencias entre materias
- Validación de equivalencias (no circular, misma carrera, etc.)
- Aplicación automática de equivalencias al cambiar estado de materia

**APIs:**

```
GET    /api/equivalences?userId={uuid}
POST   /api/equivalences
GET    /api/equivalences/{id}
PUT    /api/equivalences/{id}
DELETE /api/equivalences/{id}
GET    /api/subjects/{id}/equivalences
```

**Estimación:** 3-4 días

---

#### 🔥 Prioridad 4: Auth Module

**Funcionalidad:**

- Validación de JWT de Supabase
- Extracción de user_id del token
- Filtros de seguridad (todas las APIs requieren auth)
- Validación de ownership (usuario solo accede a sus datos)

**Componentes:**

- AuthJwtAuthenticationFilter
- AuthSessionService
- JwtService (modular)
- AuthModuleController
- SecurityConfig
- Integracion con Supabase Auth (credenciales delegadas)

**Estimación:** 2-3 días

---

### Lógica de Negocio Crítica

### 2.1 Validación de Correlativas (Prerequisitos)

**Funcionalidad:**

- Verificar si materia está bloqueada por correlativas
- Calcular qué materias se habilitan al aprobar una
- Validar que no haya dependencias circulares
- Estado "bloqueada" dinámico

**Lógica:**

```
Materia BLOQUEADA si:
- tiene correlativas Y
- alguna correlativa NO está en estado "aprobada"

Materia DISPONIBLE si:
- no tiene correlativas O
- todas las correlativas están "aprobadas"
```

**APIs:**

- GET /api/subjects/:id/status (incluye si está bloqueada)
- GET /api/subjects/:id/prerequisites (lista correlativas)
- GET /api/subjects/:id/enables (qué materias habilita esta)
- GET /api/careers/:id/available-subjects (materias disponibles para cursar)

### 2.2 Cálculo de Progreso

**Métricas a calcular:**

- Total de materias / materias aprobadas / cursando / pendientes / bloqueadas
- Créditos completados / totales
- Porcentaje de avance (por materias y por créditos)
- Promedio general
- Promedio por año/semestre
- Estimación de fecha de graduación

**APIs:**

- GET /api/careers/:id/progress (progreso completo)
- GET /api/careers/:id/stats (estadísticas generales)
- GET /api/careers/:id/timeline (proyección temporal)

### 2.3 Validaciones de Negocio

**Reglas a implementar:**

- No aprobar materia si correlativas no aprobadas
- No cambiar estado a "cursando" si está bloqueada
- Recalcular progreso al cambiar estado de materia
- Validar que año/semestre sean coherentes
- No permitir equivalencias circulares

**Service Layer robusto:**

- Todas las validaciones en servicios (no controllers)
- Transacciones para operaciones complejas
- Logging de operaciones importantes
- Manejo de excepciones claras

### 2.4 Búsqueda y Filtros

**Funcionalidades:**

- Buscar materias por nombre, código
- Filtrar por estado, año, semestre
- Ordenar por diferentes criterios
- Búsqueda de materias disponibles para cursar

### 2.5 Endpoints de Dashboard

**Datos para vista principal:**

- Próximas materias a cursar (recomendaciones)
- Materias en curso actuales
- Estadísticas generales
- Alertas (ej: materias con correlativas próximas a aprobar)

**Criterio de éxito Fase 2:**

```
✅ 4 módulos MVP implementados (Career, Subject, Equivalence, Auth)
✅ Validación de correlativas funcionando
✅ Cálculo de progreso correcto
✅ Validaciones de negocio implementadas
✅ APIs REST completas y documentadas (Swagger)
✅ Tests > 80% coverage por módulo
✅ Performance < 500ms por request
✅ Comunicación entre módulos via interfaces/eventos
```

---

## 🚀 Fase 3: Módulos Complementarios + Auditoría

> **Timeline:** 3-4 semanas  
> **Progreso:** 60% → 90%  
> **Objetivo:** Implementar módulos de eventos, recordatorios, configuración de usuario y sistema de auditoría

### Módulos a Implementar

#### 🟡 Event Module

**Funcionalidad:**

- CRUD de eventos (exámenes, entregas, trabajos prácticos)
- CRUD de event_schedules (horarios específicos de eventos)
- Relación eventos ↔ materias (muchos a muchos)
- Filtrado por fecha, tipo, materia
- Vista de calendario de eventos

**APIs:**

```
GET    /api/events
POST   /api/events
GET    /api/events/{id}
PUT    /api/events/{id}
DELETE /api/events/{id}
GET    /api/events?from={date}&to={date}    → eventos en rango
GET    /api/events?subjectId={uuid}         → eventos de materia
POST   /api/events/{id}/subjects/{subjectId}  → vincular materia
DELETE /api/events/{id}/subjects/{subjectId}  → desvincular materia
```

**Estimación:** 4-5 días

---

#### 🟡 Reminder Module

**Funcionalidad:**

- CRUD de recordatorios
- Relación recordatorios ↔ materias (muchos a muchos)
- Sistema de notificaciones (preparación)
- Recordatorios recurrentes vs únicos

**APIs:**

```
GET    /api/reminders
POST   /api/reminders
GET    /api/reminders/{id}
PUT    /api/reminders/{id}
DELETE /api/reminders/{id}
GET    /api/reminders?subjectId={uuid}
POST   /api/reminders/{id}/subjects/{subjectId}
DELETE /api/reminders/{id}/subjects/{subjectId}
```

**Estimación:** 3-4 días

---

#### 🟡 UserSettings Module

**Funcionalidad:**

- CRUD de configuración personalizada
- Preferencias de visualización (tema, formato hora, etc.)
- Configuración del planner (hora inicio/fin, primer día semana)
- Preferencias de notificaciones
- Un único settings por usuario

**APIs:**

```
GET    /api/settings          → obtener configuración del usuario autenticado
PUT    /api/settings          → actualizar (upsert)
POST   /api/settings/reset    → restaurar a defaults
```

**Lógica especial:**

- Crear settings por defecto al primer acceso
- Validar rangos (planner_start_hour < planner_end_hour)
- Response incluye valores efectivos (con defaults si no existen)

**Estimación:** 2-3 días

---

#### 🟡 Audit Module

**Funcionalidad:**

- Registro automático de acciones críticas
- Logging a nivel de aplicación (no DB triggers)
- Información de auditoría: acción, tabla, old/new values (JSONB), IP, user agent
- Query de logs por usuario, acción, tabla, fecha
- Implementación con AOP (Aspect Oriented Programming)

**APIs:**

```
GET    /api/audit?userId={uuid}&action={action}&from={date}&to={date}
GET    /api/audit/{id}
```

**Implementación:**

```java
@Aspect
@Component
public class AuditAspect {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @AfterReturning("@annotation(Auditable)")
    public void logAction(JoinPoint joinPoint) {
        // Capturar acción, usuario, IP, old/new values
        // Guardar en audit_logs
    }
}

// Uso en servicios:
@Service
public class SubjectService {
    @Auditable(action = "UPDATE_SUBJECT")
    public SubjectResponseDto updateSubject(UUID id, SubjectRequestDto dto) {
        // lógica de actualización
    }
}
```

**Acciones auditables importantes:**

- Creación/modificación/eliminación de carreras
- Cambios de estado en materias
- Modificación de correlativas
- Creación/modificación de equivalencias
- Cambios en configuración de usuario

**Estimación:** 3-4 días

---

### Integraciones entre módulos

**Event ↔ Subject:**

- Al listar eventos, incluir datos de materias vinculadas
- Al eliminar materia, desvincular de eventos (no eliminar eventos)
- Validar que materia pertenece al usuario antes de vincular

**Reminder ↔ Subject:**

- Similar a eventos
- Preparar base para notificaciones automáticas (Fase 4)

**Criterio de éxito Fase 3:**

```
✅ 4 módulos complementarios implementados (Event, Reminder, UserSettings, Audit)
✅ Sistema de auditoría capturando acciones críticas
✅ Relaciones muchos-a-muchos funcionando correctamente
✅ Validaciones de ownership en vinculaciones
✅ APIs documentadas en Swagger
✅ Tests de integración entre módulos
✅ Tests > 75% coverage
```

---

## 🎯 Fase 4: Módulo de Soporte y Producción

> **Timeline:** 1-2 semanas  
> **Progreso:** 90% → 100%  
> **Objetivo:** Implementar soporte, optimizar performance y preparar para producción

### Módulo Final

#### 🟢 Support Module

**Funcionalidad:**

- CRUD de tickets de soporte
- Tipos: REPORTE_PROBLEMA, SUGERENCIA
- Estados: ABIERTO, EN_PROCESO, RESUELTO, CERRADO
- Solo usuarios ven sus tickets
- Admin ve todos (futuro)

**APIs:**

```
GET    /api/support/tickets
POST   /api/support/tickets
GET    /api/support/tickets/{id}
PUT    /api/support/tickets/{id}
GET    /api/support/tickets?status={status}&type={type}
```

**Estimación:** 2-3 días

---

### Optimizaciones

#### Performance

- **Índices DB:** Verificar que todos los índices necesarios existen
- **Query optimization:** N+1 queries → JOIN FETCH
- **Caching:** Spring Cache en endpoints de solo lectura
- **Pagination:** Implementar paginación en listados largos

```java
@GetMapping("/api/subjects")
public Page<SubjectResponseDto> getSubjects(
    @RequestParam UUID careerId,
    @PageableDefault(size = 20) Pageable pageable
) {
    return subjectService.getSubjectsByCareer(careerId, pageable);
}
```

#### Seguridad

- Rate limiting (Spring Security)
- Validación de inputs (JSR-303 Bean Validation)
- SQL Injection prevention (JPA prepared statements)
- CORS configurado correctamente
- Headers de seguridad (X-Frame-Options, X-Content-Type-Options, etc.)

#### Observabilidad

- **Logging estructurado:** Logback con JSON
- **Métricas:** Spring Boot Actuator + Micrometer
- **Health checks:** `/actuator/health`
- **Monitoring:** Preparar para Prometheus/Grafana

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

#### Documentación

- **Swagger UI:** Accesible en `/swagger-ui.html`
- **API Docs:** OpenAPI 3.0 spec en `/v3/api-docs`
- **Postman Collection:** Exportar y versionar
- **README.md:** Instrucciones de desarrollo y deployment

**Criterio de éxito Fase 4:**

```
✅ 9 módulos completos y funcionando
✅ Sistema de soporte operativo
✅ Performance optimizado (< 300ms avg)
✅ Seguridad hardening completo
✅ Monitoring y health checks configurados
✅ Documentación Swagger completa
✅ Tests > 80% coverage global
✅ CI/CD pipeline configurado
✅ Backend listo para producción
```

## 🔌 Integración con Frontend

> **Nota:** La integración con el frontend se realizará en un plan separado, una vez que el backend MVP esté completo.

### Requisitos para Integración

**Backend debe entregar:**

```
✅ APIs REST completas y estables
✅ Documentación Swagger actualizada
✅ Guía de autenticación (JWT Supabase)
✅ Ejemplos de requests/responses
✅ Manejo de errores estandarizado
✅ CORS configurado para frontend
✅ Environment variables documentadas
```

### Puntos de Integración

#### 1. Autenticación

**Frontend debe:**

- Obtener JWT de Supabase al login
- Incluir JWT en header `Authorization: Bearer {token}`
- Renovar token antes de expiración
- Manejar errores 401 (token expirado/inválido)

#### 2. Cliente API

**Recomendación para frontend:**

```typescript
// lib/backend-client.ts
import { createClient } from "@supabase/supabase-js";

const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);

class BackendAPI {
  private baseURL = process.env.NEXT_PUBLIC_BACKEND_URL;

  private async getAuthHeader() {
    const {
      data: { session },
    } = await supabase.auth.getSession();
    return `Bearer ${session?.access_token}`;
  }

  async get<T>(path: string): Promise<T> {
    const response = await fetch(`${this.baseURL}${path}`, {
      headers: {
        Authorization: await this.getAuthHeader(),
        "Content-Type": "application/json",
      },
    });
    if (!response.ok) throw new Error(`API Error: ${response.statusText}`);
    return response.json();
  }

  // post, put, delete...
}

export const api = new BackendAPI();
```

#### 3. Manejo de Errores

**Códigos de respuesta del backend:**

- `200` - Success
- `201` - Created
- `400` - Bad Request (validación fallida)
- `401` - Unauthorized (JWT inválido/expirado)
- `403` - Forbidden (sin permisos)
- `404` - Not Found
- `409` - Conflict (ej: violación de regla de negocio)
- `500` - Internal Server Error

**Formato de error:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "La materia debe tener correlativas aprobadas",
  "path": "/api/subjects/123",
  "timestamp": "2026-03-10T14:30:00Z"
}
```

#### 4. Endpoints Disponibles

Ver documentación Swagger completa en: `{BACKEND_URL}/swagger-ui.html`

**Principales endpoints:**

- `/api/careers` - Gestión de carreras
- `/api/subjects` - Gestión de materias
- `/api/equivalences` - Equivalencias
- `/api/events` - Eventos (exámenes, entregas)
- `/api/reminders` - Recordatorios
- `/api/settings` - Configuración de usuario
- `/api/support/tickets` - Soporte

### Estrategia de Migración Gradual (Frontend)

**Opción 1: Big Bang** (No recomendada)

- Migrar todo de golpe
- Alto riesgo
- Rollback complejo

**Opción 2: Feature Flags** (Recomendada)

```typescript
const USE_BACKEND = process.env.NEXT_PUBLIC_USE_BACKEND === "true";

// Gradual: comenzar con false, ir a true por módulo
```

**Opción 3: Strangler Fig Pattern** (Recomendada para producción)

- Crear rutas nuevas que usan backend
- Mantener rutas viejas con Supabase
- Migrar usuarios progresivamente
- Deprecar rutas viejas después

### Criterios de Éxito de Integración

```
✅ Frontend consume todas las APIs del backend
✅ Autenticación funciona correctamente
✅ Performance igual o mejor que Supabase directo
✅ Funcionalidades nuevas disponibles (correlativas, cálculos)
✅ Zero downtime durante migración
✅ Rollback disponible y testeado
```

---

---

## ⚡ Timeline y Prioridades

### Plan de Desarrollo Backend MVP (10-11 semanas)

<table>
  <tr>
    <th>Fase</th>
    <th>Semanas</th>
    <th>Progreso Target</th>
    <th>Entregables Principales</th>
    <th>Estado</th>
  </tr>
  <tr>
    <td><b>Fase 0</b><br>Documentación</td>
    <td>0</td>
    <td>0% → 5%</td>
    <td>• README.md<br>• MIGRATION_PLAN.md<br>• ARCHITECTURE.md<br>• humanis_db_init.sql</td>
    <td>✅ Completo</td>
  </tr>
  <tr>
    <td><b>Fase 1</b><br>Setup Modular</td>
    <td>1-2</td>
    <td>5% → 15%</td>
    <td>• Proyecto Spring Boot<br>• DB + JPA entities<br>• JWT Auth<br>• Estructura modular</td>
    <td>🟡 En progreso</td>
  </tr>
  <tr>
    <td><b>Fase 2</b><br>MVP Core</td>
    <td>3-6</td>
    <td>15% → 60%</td>
    <td>• Career Module<br>• Subject Module (complejo)<br>• Equivalence Module<br>• Auth Module<br>• Lógica correlativas</td>
    <td>🟡 En progreso (Career iniciado)</td>
  </tr>
  <tr>
    <td><b>Fase 3</b><br>Complementarios</td>
    <td>7-9</td>
    <td>60% → 90%</td>
    <td>• Event Module<br>• Reminder Module<br>• UserSettings Module<br>• Audit Module</td>
    <td>📋 Pendiente</td>
  </tr>
  <tr>
    <td><b>Fase 4</b><br>Avanzados</td>
    <td>10-11</td>
    <td>90% → 100%</td>
    <td>• Support Module<br>• Optimización<br>• Producción ready</td>
    <td>📋 Pendiente</td>
  </tr>
</table>

### Desglose Semanal Detallado

#### Semanas 1-2: Fase 1 - Setup Modular

```
Progreso: [██░░░░░░░░░░░░░░░░░░] 5% → 15%

✅ Crear proyecto Spring Boot con Maven
✅ Configurar conexión a Supabase PostgreSQL
✅ Mapear entidades JPA (UUID, enums, arrays)
✅ Configurar Spring Security + JWT Supabase
✅ Estructura modular (config/, shared/, modules/)
✅ Foro de comunicación entre módulos (interfaces/eventos)
✅ Tests iniciales + CI/CD básico
```

#### Semanas 3-6: Fase 2 - MVP Core

```
Progreso: [████████░░░░░░░░░░░░] 15% → 60%

Semana 3:
  🏗️ Career Module (CRUD + stats)
  🏗️ Auth Module (filtros de seguridad)

Semana 4-5:
  🏗️ Subject Module - Parte 1
     • CRUD de subjects
     • subject_modules (módulos evaluables)
     • subject_schedules (horarios)

Semana 6:
  🏗️ Subject Module - Parte 2
     • Lógica de correlativas
     • Cálculos de progreso
     • Validaciones de negocio
  🏗️ Equivalence Module (CRUD + validaciones)
  🏗️ Testing exhaustivo (>80% coverage)
```

#### Semanas 7-9: Fase 3 - Módulos Complementarios + Auditoría

```
Progreso: [██████████████░░░░░░] 60% → 90%

Semana 7:
  🏗️ Event Module (CRUD + schedules + relations)
  🏗️ Reminder Module (CRUD + relations)

Semana 8:
  🏗️ UserSettings Module (CRUD + defaults)
  🏗️ Audit Module - Parte 1 (AOP setup + entidades)

Semana 9:
  🏗️ Audit Module - Parte 2 (APIs + integración con módulos)
  🏗️ Testing de integraciones
  🏗️ Verificación de auditoría en acciones críticas
```

#### Semanas 10-11: Fase 4 - Soporte y Producción

```
Progreso: [████████████████████] 90% → 100%

Semana 10:
  🏗️ Support Module (tickets de soporte)
  🏗️ Performance tuning (queries, caché, índices)
  🏗️ Seguridad hardening

Semana 11:
  🏗️ Monitoring (Actuator + métricas)
  🏗️ Documentación final
  🏗️ CI/CD pipeline
  🏗️ Deploy preparation
```

### Hitos Críticos

<table>
  <tr>
    <th>Hito</th>
    <th>Fecha Estimada</th>
    <th>Criterio de Éxito</th>
  </tr>
  <tr>
    <td>🎯 <b>Setup Completo</b></td>
    <td>Fin Semana 2</td>
    <td>Auth funciona + 1 endpoint CRUD testeado</td>
  </tr>
  <tr>
    <td>🎯 <b>MVP Core Completo</b></td>
    <td>Fin Semana 6</td>
    <td>4 módulos core funcionales + correlativas</td>
  </tr>
  <tr>
    <td>🎯 <b>Feature Complete</b></td>
    <td>Fin Semana 9</td>
    <td>8 módulos funcionando (incluye Audit, sin Support)</td>
  </tr>
  <tr>
    <td>🎯 <b>Production Ready</b></td>
    <td>Fin Semana 11</td>
    <td>9 módulos + seguridad + optimización</td>
  </tr>
  <tr>
    <td>🚀 <b>Frontend Integración</b></td>
    <td>Semana 12+</td>
    <td>Plan separado (no scope de este documento)</td>
  </tr>
</table>

### Dependencias y Riesgos

**Dependencias externas:**

- ⚠️ Acceso a Supabase PostgreSQL (connection string, credenciales)
- ⚠️ JWT Secret de Supabase para validación
- ⚠️ Infraestructura de deployment (servidor, CI/CD)

**Riesgos identificados:**

- 🔴 **Alto:** Lógica de correlativas más compleja de lo estimado → Buffer 1 semana en Fase 2
- 🟡 **Medio:** Performance de queries con UUIDs y arrays → Optimización temprana
- 🟢 **Bajo:** Comunicación entre módulos requiere refactor → Arquitectura flexible

---

## 📊 Resumen de Progreso Actual

### Estado General: 55% Completado

<table>
  <tr>
    <th>Categoría</th>
    <th>Completado</th>
    <th>En Progreso</th>
    <th>Pendiente</th>
    <th>% Total</th>
  </tr>
  <tr>
    <td><b>Documentación</b></td>
    <td>4/4</td>
    <td>0/4</td>
    <td>0/4</td>
    <td>100%</td>
  </tr>
  <tr>
    <td><b>Infraestructura</b></td>
    <td>5/5</td>
    <td>0/5</td>
    <td>0/5</td>
    <td>100%</td>
  </tr>
  <tr>
    <td><b>Módulos MVP</b></td>
    <td>2/4</td>
    <td>1/4</td>
    <td>1/4</td>
    <td>50%</td>
  </tr>
  <tr>
    <td><b>Módulos Complementarios</b></td>
    <td>0/4</td>
    <td>0/4</td>
    <td>4/4</td>
    <td>0%</td>
  </tr>
  <tr>
    <td><b>Módulos Avanzados</b></td>
    <td>0/1</td>
    <td>0/1</td>
    <td>1/1</td>
    <td>0%</td>
  </tr>
  <tr>
    <td><b>Testing</b></td>
    <td>2/9</td>
    <td>1/9</td>
    <td>6/9</td>
    <td>25%</td>
  </tr>
  <tr>
    <td><b>Optimización</b></td>
    <td>0/4</td>
    <td>0/4</td>
    <td>4/4</td>
    <td>0%</td>
  </tr>
  <tr>
    <td colspan="4" align="right"><b>TOTAL GENERAL:</b></td>
     <td><b>55%</b></td>
  </tr>
</table>

### Elementos Completados ✅

1. ✅ README.md, MIGRATION_PLAN.md y ARCHITECTURE.md actualizados con estado auditado.
2. ✅ Dependencias y datasource alineados a PostgreSQL (`pom.xml` + `application.properties`).
3. ✅ `humanis_db_init.sql` consolidado como script unico de init limpio.
4. ✅ `Career` entity/repository implementados sobre UUID.
5. ✅ `CareerService` con CRUD basico, ownership y validaciones minimas.
6. ✅ DTOs de Career (`create`, `update`, `response`) implementados.
7. ✅ `CareerController` con endpoints CRUD, filtros y endpoint admin de metricas.
8. ✅ Base modular compartida creada (`shared/api`, `shared/event`, `shared/util`, `shared/exception`).
9. ✅ Integracion inicial Career/Subject via contratos y eventos (sin imports directos).
10. ✅ Build compilando en verde (`BUILD SUCCESS`).
11. ✅ Event/Reminder legacy desacoplados del MVP (respuesta `410 GONE`).
12. ✅ Hardening en Career: `userId` desde token + endpoint admin protegido por rol.
13. ✅ Swagger/OpenAPI habilitado (`/swagger-ui.html` y `/v3/api-docs`).
14. ✅ Auth modular independiente en `modules/auth` (`AuthModuleController`, `AuthSessionService`, `AuthJwtAuthenticationFilter`, `JwtService`).
15. ✅ Eliminacion de clases legacy de auth para evitar mezcla con codigo nuevo.
16. ✅ Tests de auth modular en verde (`26/26`): service + filter + controller + security config.

### Próximos Pasos Inmediatos

```
  1. 🔌 Adaptar frontend al contrato actual de Career/Auth (cuando toque su migracion)
    └─ Sin `userId` en path/query y respetando endpoint admin por rol
    └─ Login/register/change-password quedan delegados a Supabase Auth

  2. 🧱 Avanzar módulos MVP pendientes
    └─ Subject + Equivalence sobre el esquema UUID objetivo

  3. 🧪 Subir cobertura de tests
    └─ Empezar por CareerService/CareerController y rutas legacy críticas
```

---

## 📚 Referencias y Recursos

- **[README.md](README.md)** - Overview del proyecto
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Guía de arquitectura detallada
- **[humanis_db_init.sql](humanis_db_init.sql)** - Schema PostgreSQL actualizado (init único)

### Documentación Externa

- [Spring Boot 3.5 Docs](https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Supabase Auth with JWT](https://supabase.com/docs/guides/auth/auth-helpers/auth-custom-backend)
- [PostgreSQL UUID Type](https://www.postgresql.org/docs/current/datatype-uuid.html)

---

<div align="center">

**🚀 Backend MVP en Desarrollo**

Progreso: 45% | Módulos: 1/9 | Tests: 5% | Docs: 100%

[Ver README](README.md) · [Ver Arquitectura](ARCHITECTURE.md) · [Ver Schema DB](humanis_db_init.sql)

</div>

---

## 🔧 Arquitectura Modular (No Necesariamente Microservicios)

> **⚠️ Aclaración Importante:** La arquitectura modular se implementa por **buenas prácticas de organización**, NO porque tengamos planes confirmados de migrar a microservicios.

### ¿Por Qué Módulos?

**Razones principales:**

1. 📚 **Organización** - Código más limpio y mantenible
2. 🧩 **Testing** - Más fácil testear por dominio
3. 👥 **Equipos** - Varios desarrolladores pueden trabajar en paralelo
4. 📊 **Escalabilidad mental** - Entender el sistema por partes
5. ✅ **Bonus:** Si en el futuro se necesita escalar, está preparado

### Microservicios: Solo Si Es Realmente Necesario

**🚧 Advertencia:** Microservicios agregan **mucha complejidad operativa**:

- Deployment múltiple
- Monitoring distribuido
- Transacciones entre servicios
- Debugging más difícil
- Latencia de red
- Sincronización de datos

**✅ El monolito modular es suficiente para:**

- Equipos pequeños (< 10 personas)
- Tráfico moderado (miles de requests/segundo)
- La mayoría de aplicaciones empresariales

### ¿Cuándo SÍ Considerar Microservicios?

**SOLO si aparecen estas señales:**

- � **Carga extrema** en UN módulo específico que necesita escalar independientemente
- 👥 **Equipos grandes** (10+) trabajando constantemente en el MISMO módulo
- 🔄 **Deploy independiente** crítico (actualizar un módulo sin tocar otros)
- 🔐 **Aislamiento de seguridad** (datos críticos en servicio separado)
- 📊 **Performance** - Un módulo es cuello de botella y necesita tecnología diferente

**❌ NO migrar a microservicios solo porque:**

- "Es lo moderno" / "Lo usan las grandes empresas"
- "Queremos aprender microservicios"
- "Nos escalabilidad futura" (YAGNI - You Aren't Gonna Need It)
- El equipo es pequeño y el monolito funciona bien

### Si REALMENTE Necesitas Extraer un Módulo

> **Nota:** Esto es una guía OPCIONAL para el futuro lejano, NO parte del roadmap actual.

#### Paso 1: Identificar Candidato

**Candidatos posibles (solo si hay problemas de escala):**

1. **subject/** - Si la lógica de correlativas se vuelve muy pesada
2. **analytics/** (futuro) - Si se agrega procesamiento intensivo de datos
3. **notifications/** (futuro) - Si el volumen de notificaciones es masivo

#### Paso 2: Preparar Módulo

**Verificar que el módulo:**

```
✅ NO tiene imports directos de otros módulos
✅ Usa solo interfaces de shared/api
✅ Comunica via eventos
✅ Tiene tests independientes > 80%
✅ DTOs están en el módulo (no shared)
✅ Tiene documentación clara
```

#### Paso 3: Crear Microservicio

```bash
# 1. Crear nuevo proyecto Spring Boot
spring init --dependencies=web,jpa,postgresql \
  --groupId=aktech.planificador \
  --artifactId=subject-service \
  subject-service

# 2. Copiar módulo completo
cp -r modules/subject/* subject-service/src/main/java/aktech/subject/

# 3. Copiar shared necesario
cp -r shared/exception subject-service/src/main/java/aktech/shared/
cp -r shared/util subject-service/src/main/java/aktech/shared/

# 4. Exponer APIs REST
# Ya están listas en subject/controller/

# 5. Configurar DB independiente (opcional)
# application.properties con su propia conexión
```

#### Paso 4: Comunicación Monolito ↔ Microservicio

**Opción 1: REST HTTP**

```java
// En monolito: modules/career/service/CareerService.java
@Service
public class CareerService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.subject.url}")
    private String subjectServiceUrl;

    public List<SubjectDto> getSubjectsByCareer(UUID careerId) {
        String url = subjectServiceUrl + "/api/subjects?careerId=" + careerId;
        return restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<SubjectDto>>() {}
        ).getBody();
    }
}
```

**Opción 2: gRPC (mejor performance)**

```proto
// subject.proto
service SubjectService {
  rpc GetSubjectsByCareer(CareerRequest) returns (SubjectListResponse);
  rpc GetSubjectStatus(SubjectRequest) returns (SubjectStatusResponse);
}
```

**Opción 3: Event Bus (asíncrono)**

```java
// RabbitMQ / Kafka
@RabbitListener(queues = "career.deleted")
public void handleCareerDeleted(CareerDeletedEvent event) {
    subjectRepository.deleteByCareerId(event.getCareerId());
}
```

#### Paso 5: Feature Flag

```yaml
# application.yml
features:
  use-subject-microservice: false # Gradual rollout

services:
  subject:
    url: ${SUBJECT_SERVICE_URL:http://localhost:8081}
```

```java
@Service
public class SubjectFacade {
    @Value("${features.use-subject-microservice}")
    private boolean useSubjectMicroservice;

    @Autowired
    private SubjectService localSubjectService;

    @Autowired
    private SubjectServiceClient microserviceClient;

    public List<SubjectDto> getSubjects(UUID careerId) {
        if (useSubjectMicroservice) {
            return microserviceClient.getSubjects(careerId);
        }
        return localSubjectService.getSubjects(careerId);
    }
}
```

### Arquitectura Target con Microservicios

```
                     ┌─────────────┐
                     │   Gateway   │
                     │  (API GW)   │
                     └──────┬──────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
    ┌────▼────┐      ┌──────▼──────┐    ┌─────▼─────┐
    │ Career  │      │  Subject    │    │   Auth    │
    │ Service │      │  Service    │    │  Service  │
    │(monolito│      │(microserv.) │    │(monolito) │
    └────┬────┘      └──────┬──────┘    └─────┬─────┘
         │                  │                  │
         └──────────┬───────┴──────────┬───────┘
                    │                  │
            ┌───────▼──────┐   ┌───────▼────────┐
            │   Database   │   │  Supabase Auth │
            │  PostgreSQL  │   │      (JWT)     │
            └──────────────┘   └────────────────┘
```

### Consideraciones Operacionales

**Infraestructura:**

- Service Discovery (Eureka, Consul)
- Config Server (Spring Cloud Config)
- Circuit Breaker (Resilience4j)
- API Gateway (Spring Cloud Gateway)
- Distributed Tracing (Zipkin, Jaeger)
- Centralized Logging (ELK Stack)

**Complejidad adicional:**

```
❌ Múltiples deployments
❌ Network failures entre servicios
❌ Distributed transactions (evitar)
❌ Testing más complejo (contract testing)
❌ Monitoring distribuido
❌ Debugging más difícil
```

**Recomendación:**

```
💡 Mantener monolito modular hasta que REALMENTE necesites microservicios
💡 La arquitectura modular ya te da 80% de los beneficios
💡 Solo extraer módulos con alta carga o equipos independientes
💡 Empezar con 1 microservicio, aprender, luego escalar
```

---

## 🛡️ Estrategia de Rollback

### Rollback Rápido

**Si algo falla en producción:**

1. **Cambiar Feature Flag a false**

   ```typescript
   FEATURES.USE_BACKEND_FOR_SUBJECTS = false;
   ```

   - Frontend vuelve a usar Supabase
   - Sin deploy necesario
   - Rollback en segundos

2. **Investigar Issue**
   - Logs del backend
   - Logs del frontend
   - Reportes de usuarios

3. **Fix y Re-deploy**
   - Corregir issue
   - Deploy backend
   - Re-activar feature flag

### Contingencia Total

**Si backend completamente inaccesible:**

- Frontend automáticamente usa Supabase
- Monitoreo detecta caída
- Alert a equipo
- Mantener Supabase accesible hasta que backend se recupere

### Data Consistency

**Mientras frontend puede acceder a ambos:**

- ⚠️ Usuarios podrían modificar datos en Supabase directamente
- ✅ Backend siempre lee estado actual de DB
- ✅ No hay cache que pueda desincronizar
- ✅ Eventual consistency garantizada

---

## 📊 Métricas de Éxito

### KPIs de Migración

```
✅ Uptime > 99.9% durante migración
✅ Zero data loss
✅ Latencia APIs < 500ms (p95)
✅ Error rate < 0.1%
✅ Rollback funcional en < 1 minuto
✅ Test coverage > 80%
✅ 100% features migradas en 8 semanas
```

### Monitoreo

**Herramientas:**

- Spring Boot Actuator
- Prometheus + Grafana
- Sentry para errors
- Logs estructurados

**Alertas:**

- Error rate spike
- Latencia > 1s
- DB connection failures
- JWT validation failures

---

## 🎯 Próximos Pasos Inmediatos

### Checklist Actual del Desarrollo

```
[x] Proyecto Spring Boot existente y estructura modular en curso
[x] Conexión PostgreSQL configurada (driver + datasource)
[x] Módulo career completo (controller, service, repository, model, dto)
[x] Refactor documental y SQL base alineados al estado actual
[x] Capa shared modular implementada (`shared/api`, `shared/event`, `shared/util`, `shared/exception`)
[x] Integracion inicial de comunicacion entre modulos (CareerApi + eventos/listener base en Subject)
[x] Build en verde + Event/Reminder desacoplados del MVP (`410 GONE`)
[x] Integrar extracción de userId desde JWT en endpoints de Career
[x] Restringir endpoint admin de Career por rol
[x] Completar migracion auth a JWT emitido por Supabase (backend modular + credenciales delegadas a Supabase)
[x] Eliminar implementacion legacy de auth (controller/service/dto/filter/jwt)
[x] Agregar tests de auth modular (service + filter + controller + security)
[ ] Adaptar frontend al contrato de Career sin `userId` en request (pendiente cuando toque migracion frontend)
[ ] Avanzar Subject Module sobre esquema UUID objetivo (diferido hasta cerrar Career)
[ ] Avanzar Equivalence Module sobre esquema UUID objetivo (diferido hasta cerrar Career)
[x] Configurar Swagger/OpenAPI para endpoints nuevos
[ ] Escribir unit tests por módulo (prioridad: Career)
[x] Validar y mantener regla de no imports directos entre módulos (guardrail automatizado)
[x] Agregar test de arquitectura para aislar módulos nuevos de capas legacy (`ModuleBoundariesTest`)
```

### Recomendaciones de Implementación

**Orden sugerido de módulos:**

1. **shared/** - Base común (exceptions, DTOs genéricos)
2. **auth/** - Validación JWT Supabase
3. **career/** - Módulo más simple, ideal para template
4. **subject/** - Módulo complejo con lógica de negocio
5. **equivalence/** - Módulo opcional, menor prioridad

**Template de módulo:**
Cada módulo nuevo debe seguir este patrón:

```
modules/[nombre]/
├── controller/       → REST endpoints
├── service/         → Lógica de negocio
│   └── impl/        → Implementaciones
├── repository/      → Acceso a datos
├── model/           → Entidades JPA
└── dto/             → Request/Response DTOs
```

---

<div align="center">

**Última Actualización:** 14 de Marzo, 2026

[📚 Ver README](README.md) · [🐛 Reportar Issue](#) · [✨ Sugerir Mejora](#)

</div>
