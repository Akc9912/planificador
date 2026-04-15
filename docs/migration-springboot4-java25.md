# Guía de Migración a Spring Boot 4 y Java 25

Actualmente, el proyecto está configurado para utilizar **Java 21 (LTS)** y **Spring Boot 3.x**, que ofrecen un marco robusto, estable y moderno. Sin embargo, para mantener el sistema seguro y aprovechar las futuras mejoras de rendimiento y lenguaje, está previsto un salto generacional hacia **Java 25 (próximo LTS)** y **Spring Boot 4**.

Este documento establece las directrices de **cuándo** y **cómo** llevar a cabo esta migración.

---

## 1. ¿Cuándo debemos migrar? (Triggers de Migración)

La migración no debe hacerse el "Día 1" del lanzamiento de estas tecnologías. Debe planificarse bajo las siguientes condiciones:

1. **Disponibilidad y Madurez (GA):** 
   - Java 25 sea liberado oficialmente como versión LTS (Long Term Support) - *Proyectado para Septiembre 2025*.
   - Spring Boot 4 alcance su versión General Availability (GA) y preferiblemente haya recibido al menos un par de parches de estabilidad (ej. `4.0.2` o `4.1.x`).
2. **Soporte del Ecosistema:** Todas nuestras dependencias críticas (PostgreSQL driver, Supabase Auth/JWT, MapStruct/ModelMapper si se usan, Hibernate) declaren soporte oficial para Java 25 y Spring Boot 4.
3. **Fin de Soporte (EOL):** Cuando Spring Boot 3.x anuncie su fin de soporte gratuito (OSS Support End) o actualizaciones de seguridad.
4. **Presión Técnica / Features:** Si Java 25 consolida características fuertemente deseadas (como *Valhalla / Value Types*, *Pattern Matching* avanzado) o Spring Boot 4 ofrece reducciones críticas en tiempos de arranque/consumo de memoria que el proyecto necesite urgentemente.

---

## 2. Preparación Previa a la Migración

Antes de cambiar las versiones en el `pom.xml`, se deben tomar las siguientes medidas proactivas en la versión actual (Spring Boot 3.x):

- **Resolver Deprecaciones:** Spring Boot 4 eliminará cualquier API, clase o método que haya sido marcado como `@Deprecated` en Spring Boot 3.x. Revisa los warnings de compilación (`mvn clean compile -Xlint:deprecation`) periódicamente y actualiza el código.
- **Actualizar Jakarta EE:** Mantener las anotaciones de Jakarta (`jakarta.persistence.*`, `jakarta.validation.*`) al día, ya que Spring Boot 4 seguramente adopte una versión más estricta o nueva de Jakarta EE.
- **Cobertura de Tests:** Asegurar que los módulos críticos tengan una cobertura de tests (Unitarios e Integración) superior al 80%. Esta será nuestra red de seguridad durante la actualización.

---

## 3. ¿Cómo Migrar? (Paso a Paso)

Una vez tomada la decisión, la migración se ejecutará en una rama aislada (`feature/upgrade-sb4-java25`) siguiendo estos pasos:

### Paso 1: Actualizar el entorno de desarrollo y CI/CD
- Instalar el JDK 25 localmente.
- Actualizar el `Dockerfile` base: `FROM eclipse-temurin:25-jre-alpine` (o la imagen equivalente).
- Actualizar las acciones de GitHub/GitLab para usar `java-version: '25'`.

### Paso 2: Actualizar `pom.xml`
Modificar las propiedades de versión en Maven:
```xml
<properties>
    <java.version>25</java.version>
</properties>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.x.x</version> <!-- Usar la versión estable objetivo -->
</parent>
```

### Paso 3: Actualizar Dependencias de Terceros
- Revisar y actualizar versiones de drivers de base de datos (`postgresql`), herramientas de migración (`flyway` / `liquibase`), y utilidades (`lombok`, `jjwt`).
- Ejecutar `mvn versions:display-property-updates` para identificar qué dependencias necesitan subir de versión para ser compatibles.

### Paso 4: Ajustes de Código y Propiedades
- **application.yml / properties:** Spring Boot 4 puede renombrar o eliminar propiedades de configuración. Revisar el *Spring Boot 4 Migration Guide* oficial y aplicar los cambios en `src/main/resources/application.yml`.
- **Hibernate / JPA:** Revisar si hay cambios en el dialecto o en la forma de generar esquemas.
- **Security:** Spring Security suele tener refactorizaciones importantes en cambios de versión mayor. Verificar que el `SecurityConfig` (Configuración de OAuth2/JWT y CORS) siga compilando y funcionando correctamente.

### Paso 5: Pruebas y Validación
- Ejecutar la suite completa de pruebas: `mvn clean test`.
- Levantar el entorno con Docker Compose y ejecutar pruebas manuales de los endpoints críticos usando herramientas como Postman, Swagger o el mismo frontend en un entorno de *staging*.

### Paso 6: Monitoreo Post-Despliegue
- Tras hacer el *merge* y desplegar, vigilar intensamente los logs de producción en busca de excepciones de compatibilidad o fugas de memoria inherentes al nuevo runtime/framework.