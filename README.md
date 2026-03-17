<div align="center">

# 🎓 miCarrera Planner - Backend

### Migración de Monorepo a Arquitectura Separada

**Backend Spring Boot para miCarrera Planner**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring-boot)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Supabase](https://img.shields.io/badge/Supabase-Auth-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com/)

</div>

---

## 📋 Tabla de Contenidos

- [🎯 Contexto y Objetivos](#-contexto-y-objetivos)
- [🔄 Estado de Migración](#-estado-de-migración)
- [🏗️ Arquitectura](#️-arquitectura)
- [🗄️ Esquema de Datos](#️-esquema-de-datos)
- [🔐 Autenticación](#-autenticación)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [🚀 Instalación](#-instalación)
- [📋 Plan de Migración](#-plan-de-migración)
- [📚 Documentación Adicional](#-documentación-adicional)

---

## 🎯 Contexto y Objetivos

**miCarrera Planner** actualmente opera como un monorepo Next.js con Supabase como backend. Este proyecto representa la **separación del backend** hacia una arquitectura independiente con Spring Boot, sin romper la funcionalidad existente.

### ¿Por qué separar el backend?

```
✅ Lógica de negocio centralizada y compleja
✅ Soporte para múltiples clientes (Web, Mobile)
✅ Mayor control sobre validaciones y reglas de negocio
✅ Testing robusto (Unit + Integration)
✅ Escalabilidad independiente
✅ Preparación para escalar (monolito o separación si se necesita)
```

---

## 🔄 Estado de Migración

### Estado real auditado (14/03/2026)

Este backend existe como prototipo legado en adaptacion y todavia no esta conectado al sistema productivo.

| Area              | Estado actual            | Evidencia                                                                                                    |
| ----------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------ |
| Build             | FUNCIONAL                | `./mvnw -DskipTests compile` en verde (`BUILD SUCCESS`)                                                      |
| API Auth          | MODULAR FUNCIONAL        | `modules/auth` (`AuthModuleController`, `AuthSessionService`, `AuthJwtAuthenticationFilter`)                 |
| API Usuarios      | FUNCIONAL EN BASE LEGACY | `UsuarioController` + `UsuarioService`                                                                       |
| API Materias      | FUNCIONAL EN BASE LEGACY | `MateriaController` + `MateriaService`                                                                       |
| API Careers       | FUNCIONAL (MVP BASICO)   | `modules/career` con `CareerController`, `CareerService`, `CareerRepository` y DTOs                          |
| Capa Shared       | PARCIALMENTE FUNCIONAL   | `shared/api`, `shared/event`, `shared/util`, `shared/exception` + integracion inicial Career/Subject         |
| API Eventos       | DESACOPLADA DEL MVP      | Endpoints legacy responden `410 GONE` mientras Event Module queda Post-MVP                                   |
| API Recordatorios | DESACOPLADA DEL MVP      | Endpoints legacy responden `410 GONE` mientras Reminder Module queda Post-MVP                                |
| Seguridad         | ENDURECIDA (MODULAR)     | `AuthJwtAuthenticationFilter` (UUID + rol) + reglas por rol en `SecurityConfig`                              |
| Base de datos     | EN TRANSICION            | `pom.xml` y `application.properties` usan PostgreSQL, pero entidades siguen modelo `Integer` y tablas legacy |
| Testing           | PARCIAL FUNCIONAL        | Suite modular Auth/Career + `ModuleBoundariesTest` en CI (`modular-quality-gates.yml`)                      |

### Bloqueadores actuales

1. Formalizar contrato de endpoints legacy desacoplados (`410 GONE`) para Event/Reminder mientras se mantienen Post-MVP.
2. Adaptar consumidores frontend al contrato endurecido de Career/Auth cuando llegue su migracion.
3. Definir roadmap de reintroduccion de Event/Reminder como modulos Post-MVP.
4. Extender los checks CI modulares actuales a Subject/Equivalence al iniciar su migracion.

### Reglas operativas vigentes (14/03/2026)

1. El codigo legacy no agrega tests nuevos; las pruebas se escriben cuando cada dominio migra a modulo.
2. La migracion de `UUID` se ejecuta de forma incremental por modulo.
3. El backend no se conecta ni se usa en produccion hasta completar el MVP modular correctamente testeado.

### Proximos hitos actualizados

1. Mantener `mvn compile` en verde con control continuo de regresiones.
2. Extender tests de servicio/controlador al siguiente modulo migrado (Subject).
3. Iniciar refactor de Subject/Equivalence hacia UUID + nombres de tablas objetivo con enfoque incremental por modulo.
4. Preparar handoff de contrato API de Career/Auth para frontend cuando toque su migracion.
5. Escalar el workflow de calidad modular en CI para validar nuevos modulos migrados.

---

## 🧭 Arquitectura de Referencia

### 🎯 Arquitectura ACTUAL (Monorepo)

```
┌──────────────────────────┐
│   Next.js Full-Stack     │
│                          │
│  • Frontend (React)      │
│  • Server Components     │
│  • API Routes            │
│                          │
│        ↓ SDK             │
│                          │
│  Supabase (BaaS)         │
│  • PostgreSQL            │
│  • Auth (JWT)            │
│  • Row Level Security    │
└──────────────────────────┘
```

**Estado:** ✅ **PRODUCCIÓN ACTUAL**

- Frontend accede directamente a Supabase vía SDK
- Lógica de negocio distribuida entre frontend y database
- RLS policies manejan la seguridad

### 🏗️ Arquitectura OBJETIVO (Separada)

```
┌──────────────────┐
│  Frontend Web    │
│  (Next.js/React) │
└────────┬─────────┘
         │ REST API
         ↓
┌─────────────────────────┐
│   Spring Boot Backend   │
│                         │
│  • REST Controllers     │
│  • Business Logic       │
│  • Validations          │
│  • Progress Calc        │
│  • Prerequisites        │
└──────┬──────────┬───────┘
       │          │
       ↓          ↓
  ┌────────┐  ┌──────────┐
  │Supabase│  │PostgreSQL│
  │  Auth  │  │   DB     │
  └────────┘  └──────────┘
```

**Estado actual:** Prototipo backend en adaptacion

- Backend Spring Boot existente, todavia no integrado al flujo productivo.
- Auth modular activo en `modules/auth`; Usuarios/Materias siguen en capa legacy.
- Modulo `career` nuevo implementado en capas (`entity/repository/service/controller/dto`) con CRUD basico y ownership.
- Endpoints legacy de Eventos/Recordatorios desacoplados del MVP (respuesta `410 GONE`).
- Frontend sigue operando contra Supabase directo por ahora.
- Filtro JWT modular con claims compatibles de Supabase (`user_id`/`role`) y control de acceso admin en Career.
- Flujo Auth backend cerrado: validacion/autorizacion en backend y operaciones de credenciales delegadas a Supabase Auth.
- Tests modulares en verde para Auth/Career, con `ModuleBoundariesTest` automatizado en CI (`modular-quality-gates.yml`).
- Conexion a PostgreSQL configurada, con modelo de datos aun en transicion en parte del codigo legacy.

---

## 🏗️ Arquitectura

### 📦 Módulos del Sistema

El sistema se organiza en **9 módulos independientes**, cada uno responsable de un dominio específico:

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

### Estructura de Código Modular

El backend utiliza **arquitectura de capas organizada por módulos de dominio**:

```
src/main/java/aktech/planificador/
│
├── 📂 config/                    → Configuración global
│   ├── SecurityConfig.java       → Spring Security + JWT
│   ├── JwtConfig.java            → Validación Supabase JWT
│   ├── DatabaseConfig.java       → Conexión PostgreSQL
│   └── SwaggerConfig.java        → Documentación API
│
├── 📂 shared/                    → Código compartido
│   ├── api/                      → Interfaces de comunicación
│   │   ├── CareerApi.java
│   │   └── SubjectApi.java
│   ├── event/                    → Eventos de dominio
│   │   ├── CareerDeletedEvent.java
│   │   └── SubjectStatusChangedEvent.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── BusinessException.java
│   ├── dto/
│   │   └── GenericResponseDto.java
│   └── util/
│       └── ValidationUtils.java
│
└── 📂 modules/                   → Módulos de dominio
    │
    ├── 📦 career/                → 🔥 MVP
    │   ├── controller/
    │   │   └── CareerController.java
    │   ├── service/
    │   │   └── CareerService.java
    │   ├── repository/
    │   │   └── CareerRepository.java
    │   ├── model/
    │   │   └── Career.java
    │   └── dto/
    │       ├── CareerCreateRequestDto.java
    │       ├── CareerUpdateRequestDto.java
    │       └── CareerResponseDto.java
    │
    ├── 📦 subject/               → 🔥 MVP (más complejo)
    │   ├── controller/
    │   │   ├── SubjectController.java
    │   │   └── SubjectModuleController.java
    │   ├── service/
    │   │   ├── SubjectService.java
    │   │   ├── SubjectModuleService.java
    │   │   ├── SubjectScheduleService.java
    │   │   ├── CorrelativeValidationService.java
    │   │   └── ProgressCalculationService.java
    │   ├── repository/
    │   │   ├── SubjectRepository.java
    │   │   ├── SubjectModuleRepository.java
    │   │   └── SubjectScheduleRepository.java
    │   ├── model/
    │   │   ├── Subject.java
    │   │   ├── SubjectModule.java
    │   │   └── SubjectSchedule.java
    │   └── dto/
    │
    ├── 📦 equivalence/           → 🔥 MVP
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    │
    ├── 📦 auth/                  → 🔥 MVP
    │   ├── filter/
    │   │   └── JwtAuthenticationFilter.java
    │   ├── service/
    │   │   └── JwtValidationService.java
    │   └── dto/
    │
    ├── 📦 event/                 → 🟡 Post-MVP
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   │   ├── Event.java
    │   │   ├── EventSchedule.java
    │   │   └── EventSubject.java
    │   └── dto/
    │
    ├── 📦 reminder/              → 🟡 Post-MVP
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   │   ├── Reminder.java
    │   │   └── ReminderSubject.java
    │   └── dto/
    │
    ├── 📦 user_settings/         → 🟡 Post-MVP
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   │   └── UserSettings.java
    │   └── dto/
    │
    ├── 📦 audit/                 → � Post-MVP
    │   ├── aspect/
    │   │   └── AuditAspect.java
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   │   └── AuditLog.java
    │   └── dto/
    │
    └── 📦 support/               → 🟢 Futuro
        ├── controller/
        ├── service/
        ├── repository/
        ├── model/
        │   └── SupportTicket.java
        └── dto/
```

### Ventajas de Esta Arquitectura

<table>
  <tr>
    <th>Ventaja</th>
    <th>Descripción</th>
  </tr>
  <tr>
    <td><b>🔒 Encapsulación</b></td>
    <td>Cada módulo es independiente, con sus propias capas</td>
  </tr>
  <tr>
    <td><b>📦 Cohesión</b></td>
    <td>Todo lo relacionado a un dominio está junto</td>
  </tr>
  <tr>
    <td><b>🔄 Reutilización</b></td>
    <td>Código compartido en /shared evita duplicación</td>
  </tr>
  <tr>
    <td><b>🧪 Testabilidad</b></td>
    <td>Cada módulo se puede testear independientemente</td>
  </tr>
  <tr>
    <td><b>👥 Equipos</b></td>
    <td>Diferentes equipos pueden trabajar en módulos separados</td>
  </tr>
  <tr>
    <td><b>� Escalabilidad</b></td>
    <td>Módulos organizados, opcionalmente separables si es necesario</td>
  </tr>
</table>

### Dependencias Entre Módulos

**Reglas:**

- ✅ Módulos pueden usar código de `/shared`
- ✅ Módulos pueden comunicarse vía interfaces/eventos
- ❌ NO debe haber imports directos entre módulos
- ❌ NO circular dependencies

**Ejemplo de comunicación entre módulos:**

```java
// ❌ EVITAR: Import directo entre módulos
import aktech.planificador.modules.career.service.CareerService;

// ✅ CORRECTO: Usar interfaces/eventos o llamadas REST internas
public interface CareerApi {
    CareerResponseDto getCareer(UUID id);
}
```

### Escalabilidad

La arquitectura modular permite:

- ✅ **Escalar el monolito completo** (vertical u horizontal)
- ✅ **Cachear por módulo** (caché independiente para career vs subject)
- ✅ **Optimizar por módulo** (queries específicas, índices personalizados)
- 🔒 **Opción futura:** Extraer módulo a servicio separado (solo si es necesario)

> **Nota:** Microservicios NO son parte del plan. El monolito modular es suficiente para la mayoría de casos. La arquitectura simplemente está preparada por si en el futuro lejano se necesita separar algo por razones de escala extrema.

---

## 🗄️ Esquema de Datos

### Base de Datos ACTUAL (Supabase PostgreSQL)

El esquema actual está documentado en [humanis_db_init.sql](humanis_db_init.sql):

**Entidades principales:**

#### 📚 careers (Carreras)

- `id`: UUID
- `user_id`: UUID → auth.users
- `name`, `institution`: TEXT
- `status`: ENUM (no_iniciada, en_curso, pausada, finalizada)
- `has_hours`, `has_credits`: BOOLEAN
- Timestamps automáticos

#### 📖 subjects (Materias)

- `id`: UUID
- `career_id`: UUID → careers
- `name`, `code`: TEXT
- `status`: ENUM (pendiente, cursando, regular, aprobada, libre)
- `grade`: INTEGER (0-10)
- `correlatives`: TEXT[] (array de UUIDs prerequisitos)
- `year`, `semester`: INTEGER
- `hours`, `credits`: INTEGER
- `approval_method`: ENUM (promocion, examen_final, examen_libre)

#### 📝 subject_modules (Módulos)

- Módulos/parciales de materias
- Cada módulo tiene su propia nota

#### 🔄 equivalences (Equivalencias)

- Equivalencias entre materias
- Tipos: total o parcial

**Características:**

```
✅ UUID como IDs
✅ RLS policies activas
✅ Triggers para updated_at
✅ Array de correlativas
✅ Sistema de horas/créditos flexible
✅ Estados bien definidos
```

### Adaptación para Spring Boot

El backend Spring Boot deberá:

- Usar UUID como tipo de ID (no Integer)
- Mapear enums PostgreSQL
- Manejar arrays de correlativas
- Replicar lógica de RLS en capa de servicio
- Agregar validación de prerequisitos
- Calcular progreso dinámicamente

---

## 🔐 Autenticación

### Estrategia: Híbrida con Supabase Auth

**No reinventaremos la rueda.** Mantendremos Supabase Auth y validaremos JWTs en el backend:

```
1. Usuario → Login en Supabase Auth
2. Supabase → JWT token
3. Frontend → Request con JWT en header Authorization
4. Backend → Valida JWT con Supabase public key
5. Backend → Procesa request y retorna respuesta
```

**Configuración necesaria:**

- Supabase JWT Secret para validar tokens
- Spring Security configurado para validar JWT
- Extracción de user_id del token
- Validación de ownership en cada operación

---

## 🛠️ Stack Tecnológico

### Backend (Objetivo)

| Tecnología            | Versión | Propósito             |
| --------------------- | ------- | --------------------- |
| **Spring Boot**       | 3.5.4   | Framework principal   |
| **Java**              | 24      | Lenguaje              |
| **Spring Data JPA**   | -       | ORM y repositories    |
| **PostgreSQL Driver** | Latest  | Conexión a PostgreSQL |
| **Spring Security**   | -       | Seguridad y JWT       |
| **Maven**             | -       | Build tool            |

### Database

| Tecnología        | Propósito                       |
| ----------------- | ------------------------------- |
| **PostgreSQL 16** | Base de datos principal         |
| **Supabase**      | Auth (opcional: migrar todo DB) |

### Frontend (Existente)

| Tecnología     | Versión | Propósito |
| -------------- | ------- | --------- |
| **Next.js**    | 14+     | Framework |
| **React**      | 18+     | UI        |
| **TypeScript** | 5+      | Lenguaje  |

---

## 🚀 Instalación

### Prerrequisitos

```bash
✅ Java 24
✅ Maven 3.x
✅ PostgreSQL 16 (local o Supabase)
✅ Cuenta Supabase (para Auth)
```

### 1. Configurar Base de Datos

**Opción A: Usar Supabase existente**

```bash
# Ya tienes el schema, solo necesitas las credenciales
```

**Opción B: PostgreSQL local**

```bash
# Ejecutar humanis_db_init.sql en tu instancia local
psql -U postgres -d micarrera < humanis_db_init.sql
```

### 2. Configurar Variables de Entorno

Crear `.env` en la raiz del proyecto (puedes copiar `.env.example`):

```env
# Application
SERVER_PORT=8081

# JWT used by internal tokens
PLANNI_JWT_SECRET_KEY=replace_with_a_long_random_secret
PLANNI_JWT_EXPIRATION_MS=86400000

# Supabase Auth
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=replace_with_supabase_anon_key
SUPABASE_SERVICE_ROLE_KEY=replace_with_supabase_service_role_key
SUPABASE_JWT_SECRET=replace_with_supabase_jwt_secret

# Supabase Postgres connection
PLANNI_DB_HOST=db.your-project-ref.supabase.co
PLANNI_DB_PORT=5432
PLANNI_DB_NAME=postgres
PLANNI_DB_USER=postgres
PLANNI_DB_PASSWORD=replace_with_db_password
```

`application.properties` ya carga este archivo con `spring.config.import=optional:file:.env[.properties]`.

### 3. Instalar y Ejecutar

```bash
# Instalar dependencias
./mvnw clean install

# Ejecutar en desarrollo
./mvnw spring-boot:run
```

Backend disponible en: `http://localhost:8081`

Swagger UI disponible en: `http://localhost:8081/swagger-ui.html`
OpenAPI JSON disponible en: `http://localhost:8081/v3/api-docs`

---

## 📋 Plan de Migración

Ver documentacion de iteracion: **[Iteracion 01 - Migracion Backend MVP](docs/iteracion-01-migracion-backend-mvp/README.md)**

### Resumen de Fases

#### 🔥 Fase 1: Setup Base (1-2 semanas)

- Configurar proyecto Spring Boot
- Conectar a PostgreSQL/Supabase
- Mapear entidades JPA (UUID, enums PostgreSQL)
- Configurar validación JWT Supabase
- APIs básicas CRUD

#### 🟡 Fase 2: Lógica de Negocio (2-3 semanas)

- Validación de correlativas/prerequisitos
- Cálculo de progreso
- Validaciones complejas
- Bloqueo por prerequisitos no cumplidos
- Testing completo

#### 🟢 Fase 3: Migración Frontend (2-3 semanas)

- Cambiar Supabase client por API calls
- Mantener doble acceso durante transición
- Feature flags para rollback
- Testing E2E
- Deploy gradual

#### ⚡ Fase 4: Features Avanzadas (Futuro)

- Analytics de progreso
- Recomendaciones de materias
- Sistema de notificaciones
- Importar/exportar planes
- Templates de carreras institucionales

---

## 📊 Estado Actual del Código

### ⚠️ Situación Actual

Este repositorio contiene:

- Backend legacy funcional parcial (Usuarios, Materias) + Auth modular independiente
- Modulo `career` ya implementado en arquitectura modular con DTOs y controlador REST
- Capa `shared` creada para contratos entre modulos (APIs, eventos, validaciones y excepciones de negocio)
- Configuracion PostgreSQL aplicada en dependencias y datasource
- Script unico de base objetivo para inicializacion desde cero (`humanis_db_init.sql`)

**Necesita:**

- Consolidar handoff de contrato Auth/Career para integracion frontend cuando corresponda
- Alinear consumidores frontend al contrato endurecido de Career (sin `userId` en request)
- Definir implementacion Post-MVP para Event/Reminder (hoy desacoplados del core)
- Continuar refactor de Subject/Equivalence hacia esquema objetivo UUID
- Agregar tests de servicio/controlador para flujos criticos

---

## 📚 Documentación Adicional

Este proyecto cuenta con documentación detallada para guiar el desarrollo:

### 📋 [Documentacion de Iteracion](docs/iteracion-01-migracion-backend-mvp/README.md)

Estado detallado del plan de migracion modular, dividido en 4 fases con tareas ejecutables:

- **Fase 1:** Setup y configuracion modular (cerrada)
- **Fase 2:** MVP - Modulos core
- **Fase 3:** Modulos complementarios y auditoria
- **Fase 4:** Soporte y produccion

### 🏗️ [ARCHITECTURE.md](ARCHITECTURE.md)

Guía completa de arquitectura modular:

- Principios de diseño y patrones
- Estructura detallada de módulos
- Comunicación entre módulos (interfaces y eventos)
- Convenciones de código y nomenclatura
- Estrategias de testing
- Ejemplos prácticos de implementación

### 📝 Otros Documentos

- **[humanis_db_init.sql](humanis_db_init.sql)** - Script unico de base de datos PostgreSQL (init completo)

---

## 🎯 Próximos Pasos

### Para Continuar con el Desarrollo

1. **Leer documentación:**
   - ✅ Este README (overview general)
   - ✅ [Documentacion de iteracion](docs/iteracion-01-migracion-backend-mvp/README.md) (fases y estado actual)
   - ✅ [ARCHITECTURE.md](ARCHITECTURE.md) (patrones y estructura)

2. **Adaptar consumo de Career al contrato actual:**

- Consumir endpoints de Career sin enviar `userId` por path/query
- Mantener endpoint admin de metricas solo para rol `ADMIN`

3. **Consolidar contrato de Auth modular:**

- Mantener login/register/change-password como operaciones gestionadas por Supabase Auth
- Estandarizar consumo de `/auth/me` y `/auth/token/validate` para validacion de sesion

4. **Avanzar módulos MVP restantes y testing:**

- Subject y Equivalence sobre modelo objetivo
- Tests unitarios de `CareerService` y `CareerController`

Ver estado de tareas en [Documentacion de iteracion](docs/iteracion-01-migracion-backend-mvp/README.md).

---

## 🤝 Contribuir

### Reglas para Módulos

- ❌ NO imports directos entre módulos
- ✅ Comunicación vía interfaces (`shared/api`)
- ✅ O comunicación vía eventos (`shared/event`)
- ✅ Testing > 80% coverage por módulo
- ✅ Documentar APIs con Swagger

Ver detalles completos en [ARCHITECTURE.md](ARCHITECTURE.md).

---

<div align="center">

**Hecho con ❤️ para la comunidad estudiantil**

[🗄️ Ver Schema DB](humanis_db_init.sql) · [📋 Documentacion](docs/iteracion-01-migracion-backend-mvp/README.md) · [🏗️ Arquitectura](ARCHITECTURE.md)

</div>
