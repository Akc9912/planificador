# Análisis del Estado del Sistema: miCarrera Planner (Backend)

## 1. Visión General
**miCarrera Planner** es el backend de un sistema de gestión académica diseñado para que los estudiantes planifiquen y den seguimiento a sus carreras universitarias. Actualmente se encuentra en un estado de **MVP (Producto Mínimo Viable) funcional**, listo para ser integrado con el frontend.

## 2. Stack Tecnológico
El proyecto está construido sobre un stack robusto y moderno de Java:
- **Framework Principal:** Spring Boot 3.5.4
- **Lenguaje:** Java 21 (según configuración de `pom.xml`)
- **Persistencia de Datos:** Spring Data JPA, Hibernate (6.5.2)
- **Base de Datos:** PostgreSQL (preparado para integración con Supabase)
- **Seguridad:** Spring Security con validación JWT (Nimbus JOSE+JWT y JJWT)
- **Documentación API:** Springdoc OpenAPI (Swagger UI)
- **Build Tool:** Maven

## 3. Arquitectura del Sistema
El sistema emplea una **Arquitectura Modular (Modular Monolith)** basada en dominios, lo que facilita la mantenibilidad, escalabilidad y una futura migración a microservicios si fuese necesario.

### Estructura de Directorios Principal:
- `src/main/java/aktech/planificador/config/`: Configuraciones globales (Seguridad, JWT, Base de Datos, Swagger).
- `src/main/java/aktech/planificador/shared/`: Código compartido o *Shared Kernel* (Interfaces de API, manejo de excepciones, DTOs genéricos, utilidades).
- `src/main/java/aktech/planificador/modules/`: Contiene los dominios de negocio aislados.

### Reglas Arquitectónicas Estrictas:
- **Encapsulación:** Los módulos no pueden importarse directamente entre sí (validado mediante tests automáticos como `ModuleBoundariesTest`).
- **Comunicación:** La comunicación inter-módulo se realiza a través de interfaces públicas definidas en la capa `shared/api/`.
- **Diseño en Capas:** Cada módulo sigue el patrón `Controller -> Service -> Repository`.

## 4. Módulos de Negocio (MVP)
1. **Career (Carreras):** Gestión del ciclo de vida de las carreras del usuario (iniciada, en curso, pausada, finalizada).
2. **Subject (Materias):** Catálogo de materias, módulos evaluables (parciales), horarios y sistema de correlativas (prerrequisitos).
3. **Equivalence (Equivalencias):** Sistema para convalidar materias entre diferentes instituciones (equivalencias totales o parciales).
4. **Auth (Autenticación):** Integración con Supabase Auth. El backend actúa como validador (Stateless) de los tokens JWT emitidos por Supabase, aplicando autorización basada en roles.

## 5. Base de Datos y Modelado
- **Motor:** PostgreSQL 16.
- **Esquema Inicial:** Definido en `humanis_db_init.sql`.
- **Características Clave:**
  - Uso intensivo de `UUID` como identificadores primarios.
  - Soporte para *Row Level Security (RLS)* a nivel de base de datos.
  - Triggers para auditoría automática (`updated_at`).
  - Uso de arrays nativos de PostgreSQL para relaciones como correlativas.

## 6. Testing y Calidad
El proyecto demuestra un fuerte compromiso con la calidad del software:
- **Suite de Pruebas:** Cuenta con aproximadamente 138 tests automatizados.
- **Tipos de Tests:** Unitarios, de integración y tests de arquitectura (verificación de límites/boundaries entre módulos).
- **Estado Actual:** El build es estable y todos los tests compilan y pasan exitosamente (`BUILD SUCCESS`).

## 7. Conclusión y Próximos Pasos
El backend se encuentra en una fase madura para su alcance MVP. La arquitectura modular impuesta desde el día cero prevendrá el acoplamiento excesivo o "código espagueti" a medida que el proyecto crezca.

**Siguientes pasos recomendados:**
1. **Integración con el Frontend:** Comenzar el consumo de la API RESTful documentada en Swagger (`/swagger-ui.html`) desde la aplicación cliente (Next.js/React).
2. **Monitoreo de la Integración con Supabase:** Asegurar que el flujo híbrido (Login en cliente con Supabase -> Request con JWT -> Validación en Spring Boot) funcione correctamente en entornos desplegados.
3. **CI/CD:** Configurar pipelines (e.g., GitHub Actions) utilizando el `Dockerfile` y `deploy.sh` existentes para automatizar pruebas y despliegues.