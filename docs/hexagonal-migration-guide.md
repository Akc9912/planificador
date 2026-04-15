# Guía de Migración a Arquitectura Hexagonal

Actualmente, el proyecto está estructurado con una arquitectura **Modular Monolith** enfocada en **Vertical Slices (Feature-Driven)**. Esto significa que cada módulo (`professional`, `serviceoffering`, etc.) es un bloque plano y cohesivo que contiene su controlador, su entidad JPA, su repositorio y su servicio (lógica).

Este enfoque es excelente para mantener el código simple (CRUDs, lógicas directas) y no sobre-ingenierizar componentes pequeños. Sin embargo, a medida que la aplicación crece, ciertos módulos evolucionarán hasta contener lógicas de dominio complejas o interacciones densas con servicios externos.

Cuando esto ocurre, es momento de migrar **solo ese módulo específico** a una **Arquitectura Hexagonal (Ports & Adapters)**.

---

## 1. ¿Cuándo migrar un Módulo?

Migraremos un módulo de *Vertical Slices* a *Hexagonal* cuando se cumplan **dos o más** de las siguientes condiciones:

### 1.1 Múltiples Fuentes de Datos / API Externas
El módulo comienza a requerir comunicación con servicios de terceros (Notificaciones por WhatsApp, pasarelas de pago como MercadoPago/Stripe, facturación, CRMs, etc.). La Hexagonal ayuda a crear **Puertos (Interfaces)** para abstraer estas implementaciones (Adaptadores) y evitar acoplamiento de infraestructura en la lógica central.

### 1.2 Lógica de Negocio Compleja (Rich Domain)
El modelo deja de ser un simple "contenedor de datos" (Anemic Domain Model) y pasa a requerir reglas de negocio pesadas. Por ejemplo, el módulo `appointment` (Turnos): requiere calcular franjas horarias disponibles cruzando agendas, reglas de cancelación penalizadas, transiciones de estado complejas (`PENDING`, `CONFIRMED`, `CANCELED`), etc. Necesitas que la entidad sea pura y no dependa de anotaciones `@Entity` de Spring/JPA para facilitar los tests unitarios.

### 1.3 Testing Unitario Difícil
Se vuelve cada vez más complicado testear el `Service` porque está fuertemente acoplado a Repositorios Spring, Entidades JPA, o Controladores. La Arquitectura Hexagonal asegura que el núcleo de la aplicación no sepa nada del Framework.

---

## 2. ¿Cómo Migrar un Módulo? (Paso a Paso)

Recordatorio: La migración es **módulo por módulo**. El módulo A puede ser Vertical Slice, mientras el módulo B es Hexagonal.

### Paso 1: Reestructurar el Directorio del Módulo
La estructura plana actual:
```text
modules/appointment/
  api/
  controller/
  entity/
  repository/
  service/
```

Debe ser transformada a las capas de Hexagonal:
```text
modules/appointment/
  domain/                 <-- CORE PURA
    model/                <-- Appointment (Sin @Entity, sin dependencias de Spring)
    exception/            <-- Excepciones de negocio específicas
  application/            <-- CASOS DE USO Y PUERTOS
    port/
      in/                 <-- Interfaces que implementará el UseCase
      out/                <-- Interfaces que implementará la Infra (ej: AppointmentRepositoryPort)
    usecase/              <-- Implementación de los puertos de entrada (Lógica de negocio)
  infrastructure/         <-- ADAPTADORES (Frameworks, DB, HTTP)
    adapter/
      in/
        web/              <-- Antiguo 'controller/', Endpoints REST
      out/
        persistence/      <-- Antiguo 'entity/' y 'repository/', Entidades JPA
        external/         <-- Clientes HTTP a terceros (Stripe, WhatsApp)
    mapper/               <-- Mapper explicito entre Entidad JPA <-> Domain Model
```

### Paso 2: Separar el Modelo de Dominio de la Persistencia
1. El archivo que era `@Entity public class Appointment` se divide en dos:
   - `domain/model/Appointment`: Una clase plana de Java (`POJO`) con toda la lógica y reglas de validación (invariantes). No debe tener ninguna anotación de Spring o JPA.
   - `infrastructure/adapter/out/persistence/AppointmentJpaEntity`: La clase `@Entity` de persistencia pura y tonta. Solo mapea la tabla de la Base de Datos.

### Paso 3: Crear los Puertos (Ports)
1. En `application/port/out/` crear interfaces para todo lo que requiera salir del dominio (Base de datos, enviar emails): Ej: `LoadAppointmentPort`, `SaveAppointmentPort`, `SendNotificationPort`.

### Paso 4: Crear los Adaptadores de Infraestructura (Adapters)
1. En `infrastructure/adapter/out/persistence/`, crear la clase `AppointmentPersistenceAdapter`. Esta clase implementa `LoadAppointmentPort` y `SaveAppointmentPort`. Inyectará el `SpringDataRepository` nativo, buscará la entidad JPA, usará un `Mapper` para convertirla al objeto de dominio, y se lo devolverá a la capa de aplicación.

### Paso 5: Implementar el Caso de Uso (Lógica de Aplicación)
1. En `application/usecase/`, el servicio principal implementará la interfaz de entrada (Input Port). 
2. Recibirá llamadas desde el Web Adapter (Controller).
3. Usará los *Output Ports* para obtener los datos de dominio puros.
4. Ejecutará los métodos del Dominio.
5. Usará los *Output Ports* para guardar el resultado.

### Paso 6: Mantener el Contrato de API (`api/`)
Para mantener la compatibilidad con el resto de los módulos en "Vertical Slices", el módulo hexagonal debe seguir exponiendo un contrato (Facade o Input Port extendido) en la carpeta de la base del módulo o implementando un contrato explícito compartido.