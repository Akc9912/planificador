# Documentación de API - miCarrera Planner

Esta documentación describe los endpoints disponibles en el backend (Spring Boot) para ser consumidos por el Frontend (Next.js/React).

**URL Base:** `http://localhost:8081` (o la URL de despliegue)

> **Importante:** Todos los endpoints (salvo excepciones explícitas) requieren el envío del JWT obtenido de Supabase en el header `Authorization`:
> `Authorization: Bearer <tu_jwt_token>`

También puedes probar interactivamente estos endpoints usando Swagger UI en: `http://localhost:8081/swagger-ui.html`

---

## 🔐 1. Módulo de Autenticación (Auth)

El backend delega el proceso real de Login y Registro a Supabase Auth. El frontend debe utilizar el SDK de Supabase para iniciar sesión, registrarse y cambiar contraseñas. El backend actúa principalmente como validador de los tokens JWT resultantes.

| Método | Endpoint | Descripción | Body / Headers |
| :--- | :--- | :--- | :--- |
| **GET** | `/auth/me` | Devuelve la información de la sesión actual del usuario. | Header: `Authorization` |
| **POST** | `/auth/token/validate` | Valida un token de acceso y retorna sus datos. | Body: `{ "accessToken": "<jwt>" }` |
| **POST** | `/auth/login` | *Gestionado por Supabase* (El backend solo retorna un mensaje informativo). | Body: `{ "email": "...", "password": "..." }` |
| **POST** | `/auth/register` | *Gestionado por Supabase* (El backend solo retorna un mensaje informativo). | Body: `{ "email": "...", "password": "..." }` |
| **POST** | `/auth/register-admin` | *Gestionado por Supabase* (Registro con rol admin). | Body: `{ "email": "...", "password": "..." }` |
| **PUT** | `/auth/change-password`| *Gestionado por Supabase*. | Body: `{ "oldPassword": "...", "newPassword": "..." }` |

---

## 🎓 2. Módulo de Carreras (Career)

Gestión de las carreras universitarias que el usuario está cursando. Todos los endpoints validan la propiedad (*ownership*) para asegurar que el usuario autenticado solo vea/edite sus propias carreras.

| Método | Endpoint | Descripción | Request Body |
| :--- | :--- | :--- | :--- |
| **POST** | `/careers` | Crea una nueva carrera. | `{ "name": "...", "institution": "...", "startDate": "YYYY-MM-DD", "hasHours": true, "hasCredits": false }` |
| **GET** | `/careers` | Lista todas las carreras del usuario autenticado. | N/A |
| **GET** | `/careers/status/{status}` | Lista carreras filtradas por estado (ej: `ACTIVA`, `PAUSADA`). | N/A |
| **GET** | `/careers/{id}` | Obtiene el detalle de una carrera específica. | N/A |
| **GET** | `/careers/{id}/ownership`| Verifica si el usuario actual es dueño de la carrera (Devuelve `true`/`false`).| N/A |
| **PUT** | `/careers/{id}` | Actualiza los datos de una carrera existente. | `{ "name": "...", "institution": "...", ... }` |
| **DELETE**| `/careers/{id}` | Elimina una carrera de manera permanente. | N/A |

*(Admin Endpoint)*: `GET /careers/admin/status/{status}`: Devuelve métricas/carreras globales por estado (solo admins).

---

## 📚 3. Módulo de Materias (Subject)

Gestión integral de las materias dentro de una carrera.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/subjects` | Crea una materia asignada a una carrera. |
| **GET** | `/subjects/career/{careerId}` | Lista todas las materias de una carrera específica. |
| **GET** | `/subjects/career/{careerId}/status/{status}`| Filtra materias de una carrera por su estado (`aprobada`, `regular`, etc). |
| **GET** | `/subjects/career/{careerId}/search` | Busca materias con múltiples filtros (nombre, código, estado, año, semestre). |
| **GET** | `/subjects/career/{careerId}/progress`| Retorna las estadísticas/progreso de la carrera. |
| **GET** | `/subjects/career/{careerId}/dashboard`| Devuelve la info general del dashboard para esa carrera. |
| **GET** | `/subjects/{id}` | Obtiene una materia por ID (valida ownership). |
| **GET** | `/subjects/{id}/ownership` | Verifica si la materia pertenece al usuario actual (`true`/`false`). |
| **GET** | `/subjects/{id}/availability` | Consulta la disponibilidad para cursar la materia (revisa correlativas). |
| **GET** | `/subjects/{id}/unlocks/{status}` | Lista qué materias se desbloquean si la materia actual pasa a un `{status}`. |
| **PUT** | `/subjects/{id}` | Actualiza los datos de la materia (nota, estado, etc). |
| **DELETE**| `/subjects/{id}` | Elimina una materia de forma permanente. |

### 3.1 Módulos de Materia (Subject Modules)
Gestión de exámenes/parciales/trabajos dentro de una materia.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/subjects/{subjectId}/modules` | Crea un módulo para una materia. |
| **GET** | `/subjects/{subjectId}/modules` | Lista todos los módulos de una materia. |
| **PUT** | `/subjects/{subjectId}/modules/{moduleId}` | Actualiza un módulo. |
| **DELETE**| `/subjects/{subjectId}/modules/{moduleId}` | Elimina un módulo. |

### 3.2 Horarios de Materia (Subject Schedules)
Gestión de horarios cursados para la materia.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/subjects/{subjectId}/schedules` | Agrega un bloque de horario a la materia. |
| **GET** | `/subjects/{subjectId}/schedules` | Lista los horarios de la materia. |
| **PUT** | `/subjects/{subjectId}/schedules/{scheduleId}` | Actualiza un horario. |
| **DELETE**| `/subjects/{subjectId}/schedules/{scheduleId}` | Elimina un horario. |

---

## 🔄 4. Módulo de Equivalencias (Equivalence)

Sistema para declarar que una materia ha sido aprobada/convalidada mediante equivalencia de otra institución o carrera.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/equivalences` | Crea un registro de equivalencia (total o parcial). |
| **GET** | `/equivalences` | Lista todas las equivalencias del usuario autenticado. |
| **GET** | `/equivalences/subject/{subjectId}`| Obtiene la equivalencia para una materia específica. |
| **GET** | `/equivalences/{id}` | Obtiene los detalles de una equivalencia por su ID. |
| **PUT** | `/equivalences/{id}` | Actualiza la equivalencia. |
| **DELETE**| `/equivalences/{id}` | Elimina la equivalencia. |