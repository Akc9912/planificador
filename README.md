<div align="center">

# 🎓 miCarrera Planner - Backend

**Backend Spring Boot para miCarrera Planner - MVP**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring-boot)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Supabase](https://img.shields.io/badge/Supabase-Auth-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com/)

</div>

---

## 📋 Tabla de Contenidos

- [📖 Acerca del Proyecto](#-acerca-del-proyecto)
- [✨ Características MVP](#-características-mvp)
- [🏗️ Arquitectura](#️-arquitectura)
- [🗄️ Esquema de Datos](#️-esquema-de-datos)
- [🔐 Autenticación](#-autenticación)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [🚀 Instalación](#-instalación)
- [📚 Documentación Adicional](#-documentación-adicional)
- [🔗 API REST](#-api-rest)

---

## 📖 Acerca del Proyecto

**miCarrera Planner** es un sistema de gestión académica que permite a los estudiantes planificar y dar seguimiento a sus carreras universitarias. 

El backend es una **API REST construida con Spring Boot** que proporciona servicios para:
- 🎓 Gestión de carreras universitarias
- 📚 Gestión de materias y módulos
- 📐 Validación de equivalencias entre materias
- 🔐 Autenticación y autorización segura
- 📊 Cálculo automático de progreso académico

**Estado actual:** MVP funcional lista para integración con frontend.

---

## ✨ Características MVP

✅ **Gestión completa de carreras** con estados (iniciada, en curso, pausada, completada)

✅ **Catálogo de materias** con módulos, horarios y requisitos previos

✅ **Sistema de equivalencias** entre materias (total y parcial)

✅ **Autenticación con Supabase** usando JWT

✅ **Autorización basada en roles** (usuario, administrador)

✅ **Architecture por módulos independientes** con boundaries validadas

✅ **Tests automatizados** (138 tests en verde) y CI/CD modular

✅ **BaseSQL optimizada** con RLS, triggers e índices

✅ **Documentación interactiva** con Swagger UI

---

## 🏗️ Arquitectura

### 📦 Módulos del Sistema (MVP)

El backend implementa **4 módulos principales** responsables de proporcionar la funcionalidad del MVP:

| Módulo | Tablas DB | Responsabilidad |
|--------|-----------|-----------------|
| **Career** | careers | Gestión de carreras universitarias del usuario |
| **Subject** | subjects, subject_modules, subject_schedules | Gestión de materias, módulos evaluables y horarios |
| **Equivalence** | equivalences | Equivalencias entre materias de diferentes univeridades |
| **Auth** | auth.users (Supabase) | Validación JWT y autorización de requests |

### Estructura de Código Modular

El backend utiliza **arquitectura de capas organizada por módulos de dominio**:

```
src/main/java/aktech/planificador/
│
├── 📂 config/
│   ├── SecurityConfig.java       → Spring Security + JWT
│   ├── JwtConfig.java            → Validación Supabase JWT
│   ├── DatabaseConfig.java       → Conexión PostgreSQL
│   └── SwaggerConfig.java        → Documentación API
│
├── 📂 shared/                    → Código compartido (capas de transporte)
│   ├── api/                      → Interfaces sin acoplamiento
│   ├── event/                    → Eventos de dominio
│   ├── exception/                → Manejo global de excepciones
│   ├── dto/                      → Response genérico
│   └── util/                     → Utilidades compartidas
│
└── 📂 modules/
    │
    ├── 📦 career/                → Carreras universitarias
    │   ├── controller/           → REST endpoints
    │   ├── service/              → Lógica de negocio
    │   ├── repository/           → Acceso a datos
    │   ├── model/                → Entidades JPA
    │   └── dto/                  → Request/Response
    │
    ├── 📦 subject/               → Materias y módulos
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    │
    ├── 📦 equivalence/           → Equivalencias entre materias
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    │
    └── 📦 auth/                  → Autenticación y autorización
        ├── filter/               → JWT filter
        ├── service/              → Validación de tokens
        └── dto/
```

### Beneficios de Esta Arquitectura

| Beneficio | Descripción |
|-----------|-------------|
| **🔒 Encapsulación** | Cada módulo es independiente con sus propias capas |
| **📦 Cohesión** | Todo lo relacionado a un dominio está en el mismo lugar |
| **🔄 Reutilización** | Código compartido en `/shared` evita duplicación |
| **🧪 Testabilidad** | 138 tests validando módulos de forma independiente |
| **👥 Escalabilidad** | Arquitectura preparada para crecer sin duplicación |

### Reglas de Dependencias

- ✅ Módulos pueden usar código de `/shared`
- ✅ Módulos pueden comunicarse vía interfaces públicas
- ❌ NO imports directos entre módulos
- ❌ NO circular dependencies

> Las dependencias entre módulos se validan automáticamente con `ModuleBoundariesTest`

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

## � Estado del Código

### MVP - Features Completadas

✅ **Authentication & Authorization** - JWT validado con Supabase

✅ **Career Management** - CRUD completo con ownership validation

✅ **Subject Management** - Materias con módulos, horarios y correlativas

✅ **Equivalence System** - Equivalencias total/parcial entre materias

✅ **Database Schema** - PostgreSQL con RLS, triggers e índices optimizados

✅ **Testing Suite** - 138 tests automatizados con validación de boundaries

✅ **API Documentation** - Swagger UI integrado

### Calidad de Código

| Métrica | Valor | Status |
|---------|-------|--------|
| **Build** | ✅ FUNCIONAL | `./mvnw compile` passing |
| **Tests** | 138/138 | ✅ Todos en verde |
| **Architecture** | Modular | ✅ Boundaries validadas automaticamente |
| **Code Documentation** | Swagger + Comments | ✅ APIs autodocumentadas |

---

## � API REST

### Endpoints Disponibles

La API REST proporciona 4 módulos principales:

- **🔐 [Auth Module](#)** - Autenticación y validación de sesiones
- **🎓 [Career Module](#)** - Gestión de carreras universitarias
- **📚 [Subject Module](#)** - Gestión de materias, módulos y horarios  
- **🔄 [Equivalence Module](#)** - Gestión de equivalencias entre materias

### Documentación Completa

Consulta la [documentación detallada de API](docs/API.md) para:

- ✅ Descripción de todos los endpoints
- ✅ Parámetros y ejemplos de requests
- ✅ Formatos de responses
- ✅ Códigos de error
- ✅ Cómo testear con curl, Postman o JavaScript

### Quick Reference

```bash
# Swagger UI interactivo
http://localhost:8081/swagger-ui.html

# OpenAPI JSON
http://localhost:8081/v3/api-docs
```

**Todos los endpoints requieren JWT Token** (excepto login/register):
```bash
Authorization: Bearer <your_jwt_token>
```

---

## �📚 Documentación

### 🏗️ Recursos Disponibles
- **[API.md](docs/API.md)** - Documentación completa de endpoints REST para el frontend- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Guía de arquitectura modular, patrones de diseño y mejores prácticas
- **[humanis_db_init.sql](humanis_db_init.sql)** - Schema completo de la base de datos PostgreSQL

### 📖 Convenciones

El proyecto sigue las siguientes convenciones:

- **Código modular** - Cada módulo es independiente sin imports directos entre ellos
- **Capas de arquitectura** - controller → service → repository
- **Interfaces compartidas** - Comunicación entre módulos vía `/shared` (sin acoplamiento directo)
- **Testing** - Cada módulo tiene tests unitarios e integración con >80% coverage

---

## 🧪 Testing

El proyecto viene con una suite completa de tests automatizados:

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar test específico
./mvnw test -Dtest=CareerServiceTest

# Con coverage
./mvnw test jacoco:report
```

**Estado actual:** 138 tests en verde, incluyendo validación de boundaries entre módulos.

---

## 🎯 Quick Start

Opción rápida para comenzar:

### 1. Clonar y preparar

```bash
git clone <repo>
cd planificador
cp .env.example .env  # Editar con tus credenciales
```

### 2. Instalar y ejecutar

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Validar

- Backend en: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Tests: `./mvnw test`

Más detalles en la sección [Instalación](#-instalación).

---

## 🤝 Contribuir

### Reglas para el Código

✅ **DO:**
- Crear código dentro de módulos existentes en `/modules`
- Usar interfaces compartidas en `/shared` para comunicación entre módulos
- Escribir tests para todas las funciones
- Mantener burbujas de módulos (no imports directos entre ellos)

❌ **DON'T:**
- Imports directos entre módulos
- Lógica de negocio en controllers
- Tests skipped
- Cambios en `/shared` sin documentar

Mas detalles en [ARCHITECTURE.md](ARCHITECTURE.md)

---
