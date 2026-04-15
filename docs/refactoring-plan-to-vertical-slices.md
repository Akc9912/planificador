# Plan de Refactorización a Vertical Slices (Modular Monolith)

## Propósito

Este documento detalla los pasos necesarios para alinear la base de código actual del proyecto a la arquitectura oficial de **Modular Monolith (Vertical Slices)** definida en [java-backend-architecture-adaptation.md](java-backend-architecture-adaptation.md) y las convenciones de [basic-coding-guidelines.md](basic-coding-guidelines.md).

Actualmente, el proyecto utiliza una nomenclatura de paquetes orientada a capas/hexagonal (`application`, `presentation`, `persistence`, `domain/model`), la cual debe aplanarse por funcionalidad (Feature-Driven).

---

## ⚠️ Excepción a la Regla de Nomenclatura

Según la documentación original de adaptación, se sugiere el uso de un paquete llamado `enum/` dentro de cada módulo. Sin embargo, dado que **`enum` es una palabra reservada en Java**, es ilegal nombrar un paquete de esta manera. 

**Decisión:** Se utilizará el nombre de paquete **`enums/`** en todos los módulos para sortear esta restricción del lenguaje, manteniendo el espíritu de la convención.

---

## Fases de Implementación

La refactorización se realizará módulo por módulo (ej. `auth`, `career`, `equivalence`, `subject`) siguiendo estas fases:

### Fase 1: Refactorización Estructural (Reubicación de Paquetes)

Por cada módulo en `src/main/java/aktech/planificador/modules/{nombre_modulo}`:

1. **Aplanar la estructura de paquetes:**
   - Renombrar `presentation/` a `controller/`
   - Renombrar `application/` a `service/`
   - Renombrar `persistence/` a `repository/`
   - Renombrar `domain/model/` a `entity/`
   - Mantener `enums/` para las enumeraciones.

2. **Reorganización del Contrato Público (`api/`):**
   - Crear el directorio `api/` en la raíz del módulo.
   - Mover la carpeta `dto/` al interior de `api/` -> `api/dto/`.
   - Mover las interfaces públicas correspondientes al módulo desde `shared/api/` (ej. `CareerApi.java`, `SubjectApi.java`) hacia la nueva carpeta `api/` del módulo.

### Fase 2: Aplicación de Convenciones de Nombres (Naming Guidelines)

1. **Implementaciones de Servicio:** 
   - Renombrar las clases de servicio (ej. de `CareerService` a `CareerServiceImpl`).
   - Asegurar que estas clases implementen su respectivo contrato público (ej. `implements CareerApi`).
2. **Inyección de Dependencias:** 
   - Verificar que todos los `Controller` y `ServiceImpl` utilicen inyección por constructor mediante `@RequiredArgsConstructor` (Lombok) y atributos `private final`.

### Fase 3: Ajustes Core y Transversales

1. **Jerarquía de Excepciones:**
   - Crear una clase base `DomainException` en `shared/exception/`.
   - Refactorizar las excepciones existentes (`BusinessException`, `NotFoundException`) para que hereden de `DomainException`.
   - Actualizar el `GlobalExceptionHandler` para que maneje adecuadamente esta nueva jerarquía.

2. **Ajuste de Tests e Imports:**
   - Actualizar todos los imports en el código fuente afectados por el movimiento de paquetes.
   - Reflejar la misma estructura de paquetes en el directorio de pruebas (`src/test/java/`).
   - Asegurar que los nombres de las clases de prueba coincidan con las clases refactorizadas (ej. `CareerServiceImplTest`).

3. **Validación Final:**
   - Ejecutar la compilación y la suite de pruebas completa (`mvn clean compile test`).
   - Verificar que no existan dependencias circulares ni violaciones de los límites de los módulos (Module Boundaries).
### Fase 0: Renombre del paquete base
- Renombrar `aktech.planificador` a `com.micarrera`.
- Mover todas las clases de `src/main/java/aktech/planificador` a `src/main/java/com/micarrera`.
- Mover todas las clases de `src/test/java/aktech/planificador` a `src/test/java/com/micarrera`.
- Actualizar `groupId` y `artifactId` en `pom.xml`.
