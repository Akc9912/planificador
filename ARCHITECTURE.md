<div align="center">

# 🏗️ Arquitectura Modular - Guía de Desarrollo

### Patrones y Mejores Prácticas para Backend Spring Boot

**miCarrera Planner Backend**

</div>

---

## 📋 Tabla de Contenidos

- [🎯 Principios de Diseño](#-principios-de-diseño)
- [📦 Estructura de Módulos](#-estructura-de-módulos)
- [🔗 Comunicación Entre Módulos](#-comunicación-entre-módulos)
- [✅ Convenciones de Código](#-convenciones-de-código)
- [🧪 Testing](#-testing)
- [📝 Documentación](#-documentación)

---

## 🎯 Principios de Diseño

### 1. Independencia de Módulos

**Cada módulo debe:**

- ✅ Ser autónomo y funcionar independientemente
- ✅ Tener su propia lógica de negocio encapsulada
- ✅ NO depender directamente de otros módulos
- ✅ NO interactuar con código legacy desde código nuevo
- ✅ Ser fácilmente testeable y mantenible
- 🔒 Bonus: Podría separarse si alguna vez hay necesidad extrema de escala

### 1.1 Reglas Operativas de Migracion (14/03/2026)

- ✅ El codigo legacy no recibe tests nuevos; cada dominio se testea al migrar a modulo.
- ✅ La adopcion de `UUID` es incremental por modulo, no un prerequisito global.
- ✅ El backend permanece fuera de produccion hasta que el MVP modular quede correctamente testeado.

### 2. Alta Cohesión, Bajo Acoplamiento

```
✅ Alta cohesión: Todo relacionado a "Subject" está en modules/subject/
✅ Bajo acoplamiento: Módulos se comunican via interfaces/eventos
```

### 3. Single Responsibility

**Cada capa tiene una responsabilidad:**

- **Controller**: Recibir requests, validar entrada, retornar respuestas
- **Service**: Lógica de negocio, orquestación, transacciones
- **Repository**: Acceso a datos, queries
- **Model**: Representación de entidades
- **DTO**: Transferencia de datos hacia/desde cliente

---

## 📦 Estructura de Módulos

### Template de Módulo

```
modules/[nombre]/
├── controller/              → REST endpoints
│   ├── [Nombre]Controller.java
│   └── [Nombre]AdminController.java (si aplica)
│
├── service/                 → Lógica de negocio
│   ├── [Nombre]Service.java         (interface)
│   └── impl/
│       └── [Nombre]ServiceImpl.java (implementación)
│
├── repository/              → Acceso a datos
│   └── [Nombre]Repository.java
│
├── model/                   → Entidades JPA
│   ├── [Nombre].java
│   └── [Nombre]Enum.java (si aplica)
│
├── dto/                     → Data Transfer Objects
│   ├── request/
│   │   ├── Create[Nombre]RequestDto.java
│   │   └── Update[Nombre]RequestDto.java
│   └── response/
│       ├── [Nombre]ResponseDto.java
│       └── [Nombre]DetailResponseDto.java
│
└── exception/               → Excepciones específicas del módulo
    └── [Nombre]NotFoundException.java
```

### Estado Aplicado: Módulo Auth (14/03/2026)

``` 
modules/auth/
├── controller/
│   └── AuthModuleController.java
├── filter/
│   └── AuthJwtAuthenticationFilter.java
├── service/
│   ├── AuthSessionService.java
│   └── JwtService.java
└── dto/
    ├── LoginRequestDto.java
    ├── LoginResponseDto.java
    ├── RegisterRequestDto.java
    ├── RegisterResponseDto.java
    ├── ChangePasswordRequestDto.java
    ├── ChangePasswordResponseDto.java
    └── TokenValidationRequestDto.java
```

**Criterios aplicados en Auth modular:**

- ✅ `login/register/change-password` se delegan a Supabase Auth (`410 GONE` en backend).
- ✅ Backend provee validación/autorización con `/auth/me` y `/auth/token/validate`.
- ✅ El módulo `auth` no depende de clases legacy de `Controller`, `Service`, `DTO` o `Model`.
- ✅ Seguridad cableada con `AuthJwtAuthenticationFilter` en `SecurityConfig`.

### Ejemplo Completo: Módulo Subject

```
modules/subject/
├── controller/
│   └── SubjectController.java
│
├── service/
│   ├── SubjectService.java
│   ├── PrerequisiteValidationService.java
│   ├── ProgressCalculationService.java
│   └── impl/
│       ├── SubjectServiceImpl.java
│       ├── PrerequisiteValidationServiceImpl.java
│       └── ProgressCalculationServiceImpl.java
│
├── repository/
│   ├── SubjectRepository.java
│   └── SubjectModuleRepository.java
│
├── model/
│   ├── Subject.java
│   ├── SubjectModule.java
│   └── SubjectStatus.java (enum)
│
├── dto/
│   ├── request/
│   │   ├── CreateSubjectRequestDto.java
│   │   ├── UpdateSubjectRequestDto.java
│   │   └── UpdateSubjectStatusRequestDto.java
│   └── response/
│       ├── SubjectResponseDto.java
│       ├── SubjectDetailResponseDto.java
│       ├── SubjectProgressResponseDto.java
│       └── SubjectWithPrerequisitesResponseDto.java
│
└── exception/
    ├── SubjectNotFoundException.java
    └── PrerequisiteNotMetException.java
```

---

## 🔗 Comunicación Entre Módulos

### ❌ NUNCA hacer esto:

```java
// modules/subject/service/impl/SubjectServiceImpl.java
import aktech.planificador.modules.career.service.CareerService; // ❌

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private CareerService careerService; // ❌ Acoplamiento directo

    public void createSubject(CreateSubjectRequestDto dto) {
        var career = careerService.getById(dto.getCareerId()); // ❌
    }
}
```

**Problemas:**

- 🚫 Módulos acoplados directamente
- 🚫 Tests complejos (necesitas mockear el otro módulo)
- 🚫 Cambios en un módulo pueden romper el otro
- 🚫 Difícil mantener y evolucionar
- 🚫 Código nuevo interactuando con capas legacy

### ✅ Opción 1: Interfaces de Comunicación

#### Paso 1: Define interfaz en shared/

```java
// shared/api/CareerApi.java
package aktech.planificador.shared.api;

import java.util.UUID;
import aktech.planificador.shared.dto.CareerBasicDto;

public interface CareerApi {
    CareerBasicDto getCareerBasicInfo(UUID careerId);
    boolean careerExists(UUID careerId);
    boolean userOwnsCareer(UUID userId, UUID careerId);
    void validateCareerAccess(UUID userId, UUID careerId);
}
```

#### Paso 2: Implementa en el módulo career

```java
// modules/career/service/impl/CareerServiceImpl.java
@Service
public class CareerServiceImpl implements CareerService, CareerApi {

    @Override
    public CareerBasicDto getCareerBasicInfo(UUID careerId) {
        Career career = careerRepository.findById(careerId)
            .orElseThrow(() -> new CareerNotFoundException(careerId));
        return CareerMapper.toBasicDto(career);
    }

    @Override
    public boolean careerExists(UUID careerId) {
        return careerRepository.existsById(careerId);
    }

    @Override
    public boolean userOwnsCareer(UUID userId, UUID careerId) {
        return careerRepository.existsByIdAndUserId(careerId, userId);
    }

    @Override
    public void validateCareerAccess(UUID userId, UUID careerId) {
        if (!userOwnsCareer(userId, careerId)) {
            throw new UnauthorizedException("User doesn't own this career");
        }
    }
}
```

#### Paso 3: Usa en el módulo subject

```java
// modules/subject/service/impl/SubjectServiceImpl.java
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private CareerApi careerApi; // ✅ Depende de interfaz

    public SubjectResponseDto createSubject(UUID userId, CreateSubjectRequestDto dto) {
        // Validar que la carrera exista y pertenezca al usuario
        careerApi.validateCareerAccess(userId, dto.getCareerId());

        Subject subject = SubjectMapper.fromDto(dto);
        subject.setStatus(SubjectStatus.PENDIENTE);

        subject = subjectRepository.save(subject);
        return SubjectMapper.toResponseDto(subject);
    }
}
```

**Ventajas:**

- ✅ Módulos desacoplados
- ✅ Easy testing (mock la interfaz)
- ✅ Contrato claro de comunicación
- ✅ Flexibilidad para cambiar implementación

### ✅ Opción 2: Eventos de Dominio

#### Paso 1: Define eventos en shared/

```java
// shared/event/DomainEvent.java
public interface DomainEvent {
    UUID getEventId();
    LocalDateTime getOccurredAt();
}

// shared/event/career/CareerDeletedEvent.java
@Getter
@AllArgsConstructor
public class CareerDeletedEvent implements DomainEvent {
    private UUID eventId;
    private LocalDateTime occurredAt;
    private UUID careerId;
    private UUID userId;

    public static CareerDeletedEvent create(UUID careerId, UUID userId) {
        return new CareerDeletedEvent(
            UUID.randomUUID(),
            LocalDateTime.now(),
            careerId,
            userId
        );
    }
}

// shared/event/subject/SubjectStatusChangedEvent.java
@Getter
@AllArgsConstructor
public class SubjectStatusChangedEvent implements DomainEvent {
    private UUID eventId;
    private LocalDateTime occurredAt;
    private UUID subjectId;
    private UUID careerId;
    private SubjectStatus oldStatus;
    private SubjectStatus newStatus;
}
```

#### Paso 2: Publica eventos en el módulo

```java
// modules/career/service/impl/CareerServiceImpl.java
@Service
public class CareerServiceImpl implements CareerService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void deleteCareer(UUID userId, UUID careerId) {
        validateCareerAccess(userId, careerId);

        careerRepository.deleteById(careerId);

        // Publicar evento
        var event = CareerDeletedEvent.create(careerId, userId);
        eventPublisher.publishEvent(event);

        log.info("Career deleted: {}", careerId);
    }
}
```

#### Paso 3: Escucha eventos en otros módulos

```java
// modules/subject/listener/CareerEventListener.java
@Component
@Slf4j
public class CareerEventListener {

    @Autowired
    private SubjectRepository subjectRepository;

    @EventListener
    @Transactional
    public void handleCareerDeleted(CareerDeletedEvent event) {
        log.info("Handling CareerDeletedEvent for career: {}", event.getCareerId());

        // Eliminar todas las materias de la carrera
        int deletedCount = subjectRepository.deleteByCareerId(event.getCareerId());

        log.info("Deleted {} subjects for career: {}", deletedCount, event.getCareerId());
    }
}
```

#### Paso 4: Eventos asíncronos (opcional)

```java
// config/AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-event-");
        executor.initialize();
        return executor;
    }
}

// modules/subject/listener/CareerEventListener.java
@EventListener
@Async  // ✅ Procesamiento asíncrono
@Transactional
public void handleCareerDeleted(CareerDeletedEvent event) {
    // ...
}
```

**Ventajas de eventos:**

- ✅ Desacoplamiento total
- ✅ Múltiples listeners para mismo evento
- ✅ Asíncrono (no bloquea operación principal)
- ✅ Preparado para event-driven architecture
- ✅ Audit trail (puedes guardar todos los eventos)

---

## ✅ Convenciones de Código

### Nomenclatura

**Clases:**

```java
// Entidades
Career.java, Subject.java, SubjectModule.java

// Controllers
CareerController.java, SubjectController.java

// Services (interfaz + impl)
CareerService.java
CareerServiceImpl.java

// Repositories
CareerRepository.java

// DTOs
CareerResponseDto.java
CreateCareerRequestDto.java
UpdateCareerRequestDto.java

// Exceptions
CareerNotFoundException.java
```

### Anotaciones de Spring

**Controllers:**

```java
@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor  // Lombok para DI
@Validated
public class CareerController {

    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<CareerResponseDto>> getAllCareers(
        @AuthenticationPrincipal User user
    ) {
        // ...
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CareerResponseDto createCareer(
        @Valid @RequestBody CreateCareerRequestDto request,
        @AuthenticationPrincipal User user
    ) {
        // ...
    }
}
```

**Services:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)  // Por defecto read-only
public class CareerServiceImpl implements CareerService {

    private final CareerRepository careerRepository;

    @Override
    public List<CareerResponseDto> getUserCareers(UUID userId) {
        // ...
    }

    @Override
    @Transactional  // Override para operaciones de escritura
    public CareerResponseDto createCareer(UUID userId, CreateCareerRequestDto dto) {
        // ...
    }
}
```

**Repositories:**

```java
@Repository
public interface CareerRepository extends JpaRepository<Career, UUID> {

    List<Career> findByUserId(UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT c FROM Career c WHERE c.userId = :userId AND c.status = :status")
    List<Career> findByUserIdAndStatus(
        @Param("userId") UUID userId,
        @Param("status") CareerStatus status
    );
}
```

### Validaciones

**DTOs con Bean Validation:**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCareerRequestDto {

    @NotBlank(message = "Career name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @NotBlank(message = "Institution is required")
    private String institution;

    @NotNull(message = "Status is required")
    private CareerStatus status;

    private LocalDate startDate;

    private boolean hasHours;

    private boolean hasCredits;
}
```

### Manejo de Errores

**Excepciones personalizadas:**

```java
// shared/exception/BusinessException.java
public class BusinessException extends RuntimeException {
    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

// modules/career/exception/CareerNotFoundException.java
public class CareerNotFoundException extends BusinessException {
    public CareerNotFoundException(UUID careerId) {
        super("Career not found: " + careerId, "CAREER_NOT_FOUND");
    }
}

// shared/exception/GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CareerNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCareerNotFound(
        CareerNotFoundException ex
    ) {
        log.error("Career not found", ex);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDto(
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now()
            ));
    }
}
```

---

## 🧪 Testing

### Estructura de Tests

```
modules/career/
├── src/main/java/
└── src/test/java/
    └── aktech/planificador/modules/career/
        ├── controller/
        │   └── CareerControllerTest.java
        ├── service/
        │   └── impl/
        │       └── CareerServiceImplTest.java
        └── repository/
            └── CareerRepositoryTest.java
```

### Estado Actual de Testing Auth

- ✅ Unit tests de `AuthSessionService`
- ✅ Unit tests de `AuthJwtAuthenticationFilter`
- ✅ Unit tests de `AuthModuleController`
- ✅ Integration tests de reglas de `SecurityConfig` para auth
- ✅ Suite auth modular en verde: `26/26`

### Unit Tests - Service

```java
@ExtendWith(MockitoExtension.class)
class CareerServiceImplTest {

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private CareerApi careerApi;  // Si necesita otro módulo

    @InjectMocks
    private CareerServiceImpl careerService;

    private UUID userId;
    private UUID careerId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        careerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create career successfully")
    void shouldCreateCareerSuccessfully() {
        // Given
        CreateCareerRequestDto request = new CreateCareerRequestDto(
            "Ingeniería en Sistemas",
            "Universidad Nacional",
            CareerStatus.EN_CURSO,
            LocalDate.now(),
            true,
            false
        );

        Career expectedCareer = Career.builder()
            .id(careerId)
            .userId(userId)
            .name(request.getName())
            .institution(request.getInstitution())
            .status(request.getStatus())
            .build();

        when(careerRepository.save(any(Career.class))).thenReturn(expectedCareer);

        // When
        CareerResponseDto result = careerService.createCareer(userId, request);

        // Then
        assertNotNull(result);
        assertEquals(expectedCareer.getName(), result.getName());
        assertEquals(expectedCareer.getInstitution(), result.getInstitution());

        verify(careerRepository, times(1)).save(any(Career.class));
    }

    @Test
    @DisplayName("Should throw exception when career not found")
    void shouldThrowExceptionWhenCareerNotFound() {
        // Given
        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
            CareerNotFoundException.class,
            () -> careerService.getCareerById(userId, careerId)
        );

        verify(careerRepository, times(1)).findById(careerId);
    }
}
```

### Integration Tests - Repository

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CareerRepositoryTest {

    @Autowired
    private CareerRepository careerRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        careerRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find careers by user id")
    void shouldFindCareersByUserId() {
        // Given
        Career career1 = createCareer(userId, "Career 1");
        Career career2 = createCareer(userId, "Career 2");
        Career otherUserCareer = createCareer(UUID.randomUUID(), "Other");

        careerRepository.saveAll(List.of(career1, career2, otherUserCareer));

        // When
        List<Career> result = careerRepository.findByUserId(userId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getUserId().equals(userId)));
    }

    private Career createCareer(UUID userId, String name) {
        return Career.builder()
            .userId(userId)
            .name(name)
            .institution("Test University")
            .status(CareerStatus.EN_CURSO)
            .hasHours(true)
            .hasCredits(false)
            .build();
    }
}
```

### Integration Tests - API

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CareerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthJwtAuthenticationFilter jwtFilter;  // Mock auth modular para tests

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockAuthUser(userId);
    }

    @Test
    @DisplayName("Should create career via API")
    void shouldCreateCareerViaAPI() throws Exception {
        // Given
        CreateCareerRequestDto request = new CreateCareerRequestDto(
            "Ingeniería en Sistemas",
            "Universidad Nacional",
            CareerStatus.EN_CURSO,
            LocalDate.now(),
            true,
            false
        );

        // When & Then
        mockMvc.perform(post("/api/careers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(request.getName()))
            .andExpect(jsonPath("$.institution").value(request.getInstitution()))
            .andExpect(jsonPath("$.status").value(request.getStatus().toString()));
    }
}
```

---

## 📝 Documentación

### Swagger/OpenAPI

```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "miCarrera Planner API",
        version = "1.0",
        description = "API REST para gestión académica personal",
        contact = @Contact(
            name = "miCarrera Team",
            email = "support@micarrera.app"
        )
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT token from Supabase Auth"
)
public class OpenApiConfig {
}
```

**Documentar Controllers:**

```java
@RestController
@RequestMapping("/api/careers")
@Tag(name = "Careers", description = "Career management endpoints")
public class CareerController {

    @Operation(
        summary = "Get all careers",
        description = "Retrieves all careers for the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Careers retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CareerResponseDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping
    public ResponseEntity<List<CareerResponseDto>> getAllCareers() {
        // ...
    }
}
```

---

<div align="center">

**Última Actualización:** 14 de Marzo, 2026

[📚 Ver README](README.md) · [📋 Plan de Migración](MIGRATION_PLAN.md)

</div>
