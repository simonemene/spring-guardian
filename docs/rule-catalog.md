# Rule Catalog

Spring Guardian rules are deterministic and Spring-centric.

## Main families

- `SPR###` core Spring architecture, Web/API, Security, Data, configuration and production-readiness rules.
- `ARCH###` layering, DDD, hexagonal and bounded-context checks.
- `CAP###` missing Spring capability checks such as Validation, OpenAPI and Actuator.
- `ADV###` legacy-compatible Spring Alternative Advisor checks for manual Java/low-level patterns.
- `SPR_ALT###` enterprise Spring Alternatives focused on Security, Web/API, JPA, transactions, configuration, observability and modernization.
- `TX###` high-confidence transaction-boundary risks.
- `ASYNC###` `@Async`, scheduling and proxy/concurrency contracts.
- `JPA###` persistence performance/mapping risks that need JPA context.
- `CFG###` dangerous or weak runtime configuration switches.
- `WEB###`, `SEC###`, `OBS###`, `BAT###` focused Web, Security, observability and Spring Batch checks.

## Finding contract

Every grouped finding should provide:

- rule id;
- severity;
- category and technical type;
- affected file/class/line when available;
- real evidence and snippet;
- impact;
- remediation;
- Spring alternative when applicable;
- official documentation URL when useful.

## Severity semantics

| Severity | Meaning |
|---|---|
| `CRITICAL` | production blocker or severe release-readiness problem |
| `MAJOR` | fix before release |
| `MINOR` | relevant technical debt |
| `INFO` | advisory, modernization or best-practice note |

## New production-readiness rule

`SPR096_JPA_OPEN_IN_VIEW_ENABLED` detects explicit `spring.jpa.open-in-view=true` and recommends service-level transaction boundaries with DTO/projection fetch plans.


## Enterprise Spring Alternatives

The advisor catalog now includes `SPR_ALT001`-`SPR_ALT045` plus contextual advisors `SPR_ALT049` and `SPR_ALT050`.
These rules are advisory by design: they point to concrete Spring-native remediations and are grouped under **Spring Alternative Advisor** in reports.

| ID | Detects | Spring alternative |
|---|---|---|
| `SPR_ALT001_SECURITY_FILTER_CHAIN_MISSING` | Security starter without explicit `SecurityFilterChain` | explicit `SecurityFilterChain` bean |
| `SPR_ALT002_CSRF_DISABLED_WITHOUT_STATELESS_API` | CSRF disabled without stateless evidence | CSRF enabled or documented stateless API |
| `SPR_ALT003_PERMIT_ALL_TOO_BROAD` | broad public authorization matcher | granular Authorization DSL |
| `SPR_ALT004_ACTUATOR_EXPOSURE_WILDCARD` | Actuator wildcard exposure | minimal protected endpoint exposure |
| `SPR_ALT005_HEALTH_DETAILS_ALWAYS_PUBLIC` | public health details | `show-details=when_authorized` |
| `SPR_ALT006_ENTITY_RETURNED_BY_CONTROLLER` | JPA entity returned by REST controller | response DTO/projection |
| `SPR_ALT007_ENTITY_ACCEPTED_AS_REQUEST_BODY` | JPA entity accepted as request body | validated request DTO |
| `SPR_ALT008_MISSING_REQUEST_VALIDATION` | request body without `@Valid` | Bean Validation |
| `SPR_ALT009_MISSING_CONTROLLER_ADVICE` | missing centralized REST error handling | `@RestControllerAdvice` + `ProblemDetail` |
| `SPR_ALT010_OPEN_IN_VIEW_ENABLED` | Open EntityManager in View enabled | service transaction boundary + DTO fetch plan |
| `SPR_ALT011_REPOSITORY_WITH_BUSINESS_LOGIC` | repository with branching/orchestration | service layer for use cases |
| `SPR_ALT012_CONCATENATED_QUERY_STRING` | concatenated SQL/JPQL strings | bind parameters, Specifications or Querydsl |
| `SPR_ALT013_MISSING_READ_ONLY_TRANSACTION` | read service without read-only transaction | `@Transactional(readOnly = true)` |
| `SPR_ALT014_ENTITY_RELATION_EAGER_FETCH` | eager JPA relation | lazy relation + fetch join/entity graph/projection |
| `SPR_ALT015_TRANSACTIONAL_PRIVATE_METHOD` | non-interceptable transactional method | public service method or `TransactionTemplate` |
| `SPR_ALT016_TRANSACTIONAL_SELF_INVOCATION` | self-invocation of proxied methods | move boundary to another bean |
| `SPR_ALT017_TRANSACTIONAL_ON_CONTROLLER` | transaction in controller | service-level transaction boundary |
| `SPR_ALT018_CONFIGURATION_PROPERTIES_WITHOUT_VALIDATION` | properties binding without validation | `@ConfigurationProperties` + `@Validated` |
| `SPR_ALT019_SECRET_LIKE_PROPERTY` | secret-like committed property | externalized config / secret manager |
| `SPR_ALT020_SYSTEM_OUT_LOGGING` | console logging / stacktrace printing | SLF4J / structured logging |
| `SPR_ALT021_MANUAL_PRINCIPAL_SECURITY_CHECK` | `Principal` / `Authentication` null checks used as authorization | `@PreAuthorize`, `SecurityFilterChain`, `@AuthenticationPrincipal` or authorization service |
| `SPR_ALT022_SECURITY_CONTEXT_HOLDER_IN_BUSINESS_CODE` | `SecurityContextHolder` read from service/domain code | security adapter plus explicit authenticated identity/use-case parameter |
| `SPR_ALT023_MANUAL_ROLE_STRING_CHECK` | scattered `ROLE_*` string comparisons | method security, `AuthorizationManager` or typed domain permission service |

## Advanced high-signal expansion

The advanced catalog adds **69 deterministic checks/advisors**. It deliberately favors AST correlation or narrowly scoped configuration/source evidence instead of broad regex-only style checks.

### Transaction and concurrency

| ID | Detects | Severity |
|---|---|---|
| `TX101_REMOTE_CALL_INSIDE_TRANSACTION` | HTTP/Feign call from an effective `@Transactional` method | `MAJOR` |
| `TX102_READ_ONLY_TRANSACTION_WRITES` | persistence write in `readOnly=true` transaction | `MAJOR` |
| `TX103_TRANSACTIONAL_EXCEPTION_SWALLOWED` | broad exception swallowed inside a transaction | `MAJOR` |
| `ASYNC101_INVALID_ASYNC_METHOD` | non-proxyable/unsupported `@Async` method contract | `CRITICAL`/`MAJOR` |
| `ASYNC102_SCHEDULED_METHOD_HAS_PARAMETERS` | `@Scheduled` method with parameters | `CRITICAL` |

### Web, persistence, security and observability

| ID | Detects | Severity |
|---|---|---|
| `ARCH101_CONTROLLER_CALLS_CONTROLLER` | endpoint/controller invoking another controller instead of an application service | `MAJOR` |
| `WEB101_GET_WITH_REQUEST_BODY` | GET endpoint accepting `@RequestBody` | `MAJOR` |
| `JPA101_FIND_ALL_FILTER_IN_MEMORY` | `findAll().stream().filter(...)` | `MINOR` |
| `JPA102_FLUSH_INSIDE_LOOP` | persistence flush/saveAndFlush in iteration | `MAJOR` |
| `JPA103_ENTITY_LOMBOK_DATA` | JPA entity annotated with Lombok `@Data` | `MINOR` |
| `JPA104_MANY_TO_MANY_CASCADE_ALL` | `@ManyToMany` with `CascadeType.ALL` | `MAJOR` |
| `ARCH102_ENTITY_INJECTS_SPRING_BEAN` | JPA entity injecting Spring collaborators | `MAJOR` |
| `OBS101_SENSITIVE_DATA_LOGGED` | token/password/secret-like values passed to logger | `CRITICAL` |
| `OBS102_MDC_WITHOUT_CLEANUP` | `MDC.put` without local cleanup evidence | `MINOR` |
| `SEC101_NOOP_PASSWORD_ENCODER` | NoOp password encoder | `CRITICAL` |
| `SEC102_DEFAULT_PASSWORD_ENCODER` | insecure/default password encoder declaration | `MAJOR` |
| `SEC103_HARDCODED_SIGNING_SECRET` | hardcoded JWT/HMAC signing secret | `CRITICAL` |
| `SEC104_BCRYPT_LOW_STRENGTH` | explicitly weak BCrypt strength | `MAJOR` |

### Spring Batch

New Batch rules cover restart state, `RunIdIncrementer`, random identifying parameters, manual `BatchStatus` mutation, schema initialization, cursor readers in multi-threaded steps, scope/partitioning interaction, append semantics, update assertions, retry without backoff and `JobParameter` access without a Batch scope.

### Modernization advisor

The expanded advisor recognizes legacy APIs such as `WebSecurityConfigurerAdapter`, old Security matcher DSLs, `WebMvcConfigurerAdapter`, `HandlerInterceptorAdapter`, Spring Batch builder factories, `SimpleJobLauncher`, old in-memory `JobRepository`, Springfox, Sleuth and Hystrix. It also highlights contextual alternatives such as `@Async` + `@Transactional`, custom `/health` controllers, direct `HttpServletResponse` writing, blocking Reactor calls and untyped controller responses.

Advisor findings remain informational; hard runtime/architecture findings retain their own severity and quality-gate impact.
