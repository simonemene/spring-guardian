# Rule Catalog

Spring Guardian rules are deterministic and Spring-centric.

## Main families

- `SPR###` core Spring architecture, Web/API, Security, Data, configuration and production-readiness rules.
- `ARCH###` layering, DDD, hexagonal and bounded-context checks.
- `CAP###` missing Spring capability checks such as Validation, OpenAPI and Actuator.
- `ADV###` legacy-compatible Spring Alternative Advisor checks for manual Java/low-level patterns.
- `SPR_ALT###` enterprise Spring Alternatives focused on Security, Web/API, JPA, transactions, configuration and observability.

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

The unified advisor catalog now exposes `SPR_ALT001`-`SPR_ALT023`.
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
