# Endpoints de Autenticación (Auth)

Estos endpoints permiten la autenticación y gestión de sesión de usuario. La mayoría de las operaciones de login, registro y cambio de contraseña están gestionadas por Supabase Auth, pero se exponen rutas para integración y validación de sesión en el backend.

---

## 1. Obtener sesión actual

- **Método:** `GET`
- **Ruta:** `/auth/me`
- **Headers:**
  - `Authorization: Bearer <token>`
- **Descripción:**
  Devuelve la información de sesión del usuario autenticado a partir del JWT enviado en el header. Útil para validar sesión y obtener datos del usuario logueado.

---

## 2. Validar token de acceso

- **Método:** `POST`
- **Ruta:** `/auth/token/validate`
- **Body:**
  ```json
  { "accessToken": "<jwt>" }
  ```
- **Descripción:**
  Valida el token JWT recibido y retorna los datos de sesión si es válido. Útil para validar tokens en el frontend.

---

## 3. Login (gestionado por Supabase)

- **Método:** `POST`
- **Ruta:** `/auth/login`
- **Body:**
  ```json
  { "email": "<email>", "password": "<password>" }
  ```
- **Descripción:**
  Endpoint de login. La operación es gestionada por Supabase Auth, por lo que la autenticación real debe hacerse usando el SDK de Supabase en el frontend. El backend responde con un mensaje indicando que la operación es gestionada por Supabase.

---

## 4. Registro de usuario (gestionado por Supabase)

- **Método:** `POST`
- **Ruta:** `/auth/register`
- **Body:**
  ```json
  { "email": "<email>", "password": "<password>" }
  ```
- **Descripción:**
  Endpoint de registro de usuario. La operación es gestionada por Supabase Auth. Usar el SDK de Supabase en el frontend para registrar usuarios.

---

## 5. Registro de admin (gestionado por Supabase)

- **Método:** `POST`
- **Ruta:** `/auth/register-admin`
- **Body:**
  ```json
  { "email": "<email>", "password": "<password>" }
  ```
- **Descripción:**
  Similar a `/auth/register`, pero pensado para crear usuarios con rol admin. También gestionado por Supabase Auth.

---

## 6. Cambio de contraseña (gestionado por Supabase)

- **Método:** `PUT`
- **Ruta:** `/auth/change-password`
- **Body:**
  ```json
  { "oldPassword": "<old>", "newPassword": "<new>" }
  ```
- **Descripción:**
  Endpoint para cambio de contraseña. La operación es gestionada por Supabase Auth. Usar el SDK de Supabase para esta operación.

---

### Notas importantes

- **Login, registro y cambio de contraseña:** Usar siempre el SDK de Supabase en el frontend para estas operaciones. Los endpoints del backend existen solo para compatibilidad y retornan un mensaje indicando que la operación es gestionada por Supabase.
- **Validación de sesión:** Para validar si un usuario está autenticado, usar `/auth/me` o `/auth/token/validate` enviando el JWT recibido de Supabase.
- **Configuración:** El backend valida los JWT usando la clave de Supabase (`SUPABASE_JWT_SECRET`).

---

**Referencia de variables de entorno:**

- `SUPABASE_URL`: URL del proyecto Supabase
- `SUPABASE_PUBLISHABLE_KEY`: API Key pública (frontend)
- `SUPABASE_SECRET_KEY`: API Key secreta (backend)
- `SUPABASE_JWT_SECRET`: Clave JWT para validar tokens

---

Para más detalles, ver la integración en el código backend y la documentación de Supabase Auth.

# Endpoints de Career

- **POST** `/careers` — Crear carrera para usuario autenticado
- **GET** `/careers` — Listar carreras del usuario autenticado
- **GET** `/careers/status/{status}` — Listar carreras del usuario por estado
- **GET** `/careers/admin/status/{status}` — Listar carreras por estado (admin/metricas)
- **GET** `/careers/{id}` — Obtener carrera por id (valida ownership)
- **GET** `/careers/{id}/ownership` — Verificar si el usuario es dueño de la carrera
- **PUT** `/careers/{id}` — Actualizar carrera
- **DELETE** `/careers/{id}` — Eliminar carrera

## Crear carrera

- **POST** `/careers`
- **Request:**
  ```json
  {
    "name": "Ingeniería en Sistemas",
    "institution": "UTN",
    "startDate": "2022-03-01",
    "hasHours": true,
    "hasCredits": false
  }
  ```
- **Response:**
  ```json
  {
    "id": "b1a2c3d4-e5f6-7890-1234-56789abcdef0",
    "userId": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
    "name": "Ingeniería en Sistemas",
    "institution": "UTN",
    "status": "ACTIVA",
    "startDate": "2022-03-01",
    "hasHours": true,
    "hasCredits": false,
    "createdAt": "2022-03-01T10:00:00",
    "updatedAt": "2022-03-01T10:00:00"
  }
  ```

## Listar carreras del usuario autenticado

- **GET** `/careers`
- **Response:**
  ```json
  [
    {
      "id": "b1a2c3d4-e5f6-7890-1234-56789abcdef0",
      "userId": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
      "name": "Ingeniería en Sistemas",
      "institution": "UTN",
      "status": "ACTIVA",
      "startDate": "2022-03-01",
      "hasHours": true,
      "hasCredits": false,
      "createdAt": "2022-03-01T10:00:00",
      "updatedAt": "2022-03-01T10:00:00"
    }
  ]
  ```

## Listar carreras del usuario por estado

- **GET** `/careers/status/{status}`
- **Response:** igual a `/careers` pero filtrado por estado.

## Listar carreras por estado (admin/metricas)

- **GET** `/careers/admin/status/{status}`
- **Response:** igual a `/careers` pero para todos los usuarios.

## Obtener carrera por id (valida ownership)

- **GET** `/careers/{id}`
- **Response:** igual a un objeto de `/careers`.

## Verificar si el usuario es dueño de la carrera

- **GET** `/careers/{id}/ownership`
- **Response:**
  ```json
  true
  ```

## Actualizar carrera

- **PUT** `/careers/{id}`
- **Request:**
  ```json
  {
    "name": "Ingeniería en Sistemas",
    "institution": "UTN",
    "startDate": "2022-03-01",
    "hasHours": true,
    "hasCredits": false
  }
  ```
- **Response:** igual a un objeto de `/careers`.

## Eliminar carrera

- **DELETE** `/careers/{id}`
- **Response:** 204 No Content

# Endpoints de Subject

- **POST** `/subjects` — Crear materia para usuario autenticado
- **GET** `/subjects/career/{careerId}` — Listar materias de una carrera
- **GET** `/subjects/career/{careerId}/status/{status}` — Listar materias de una carrera por estado
- **GET** `/subjects/career/{careerId}/search` — Buscar materias de una carrera (filtros: nombre, código, estado, año, semestre, orden)
- **GET** `/subjects/career/{careerId}/progress` — Progreso de la carrera
- **GET** `/subjects/career/{careerId}/dashboard` — Dashboard de la carrera
- **GET** `/subjects/{id}` — Obtener materia por id (valida ownership)
- **GET** `/subjects/{id}/ownership` — Verificar si el usuario es dueño de la materia
- **GET** `/subjects/{id}/availability` — Consultar disponibilidad de la materia
- **GET** `/subjects/{id}/unlocks/{status}` — Listar materias desbloqueadas por cambio de estado
- **PUT** `/subjects/{id}` — Actualizar materia
- **DELETE** `/subjects/{id}` — Eliminar materia

# Endpoints de Equivalence

- **POST** `/equivalences` — Crear equivalencia para usuario autenticado
- **GET** `/equivalences` — Listar equivalencias del usuario
- **GET** `/equivalences/subject/{subjectId}` — Listar equivalencias por materia
- **GET** `/equivalences/{id}` — Obtener equivalencia por id (valida ownership)
- **PUT** `/equivalences/{id}` — Actualizar equivalencia
- **DELETE** `/equivalences/{id}` — Eliminar equivalencia

# Endpoints de SubjectModule

- **GET** `/subjects/{subjectId}/modules` — Listar módulos de una materia
- **POST** `/subjects/{subjectId}/modules` — Crear módulo en una materia
- **PUT** `/subjects/{subjectId}/modules/{moduleId}` — Actualizar módulo
- **DELETE** `/subjects/{subjectId}/modules/{moduleId}` — Eliminar módulo

# Endpoints de SubjectSchedule

- **GET** `/subjects/{subjectId}/schedules` — Listar horarios de una materia
- **POST** `/subjects/{subjectId}/schedules` — Crear horario en una materia
- **PUT** `/subjects/{subjectId}/schedules/{scheduleId}` — Actualizar horario
- **DELETE** `/subjects/{subjectId}/schedules/{scheduleId}` — Eliminar horario
