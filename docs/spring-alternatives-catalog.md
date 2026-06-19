# Spring Alternatives Catalog

Spring Guardian does not only flag problems. It recommends Spring-native alternatives.

## Strategic alternatives

| Problem | Spring alternative | Why |
|---|---|---|
| Field injection | Constructor injection | explicit mandatory dependencies, immutability and easier tests |
| Scattered `@Value` | `@ConfigurationProperties` with validation | typed, documented and validated configuration |
| Manual REST clients | `WebClient` or managed `RestClient`/builder bean | central configuration, interceptors, timeouts and observability |
| Missing validation | Bean Validation with `@Valid` / `@Validated` | contracts enforced at API/service boundaries |
| Manual health endpoints | Actuator health/info/metrics | operational standardization and integrations |
| Manual batch loops | Spring Batch jobs/steps | restartability, metrics and operational semantics |
| Manual event coupling | `ApplicationEventPublisher` at application boundary | clearer module communication |
| Controller transactions | service-level `@Transactional` | correct use-case transaction boundary |
| Open EntityManager in View | DTO/projection fetch plan inside service transaction | prevents hidden lazy-loading queries in the web layer |

Each alternative should explain when to use it, when to avoid it, expected remediation and official documentation link in the finding guidance.


## Unified advisor catalog

Spring alternatives are now produced by a single backend catalog: `SpringAlternativeRulesCatalog`.

The catalog keeps compatibility with existing `ADV###` ids, keeps non-duplicated `SPR067`-`SPR090` advisors that were not previously wired consistently, and adds the `SPR_ALT###` enterprise alternatives.

### Newly added alternatives

| Area | New alternatives |
|---|---|
| Security | explicit `SecurityFilterChain`, CSRF/stateless policy, granular authorization |
| Actuator | restricted endpoint exposure and protected health details |
| Web/API | DTO contracts, Bean Validation, `@RestControllerAdvice`, `ProblemDetail` |
| JPA/Data | disabled OSIV, repository/service separation, safe queries, lazy relations |
| Transactions | public service boundaries, read-only transactions, no controller transactions, no self-invocation |
| Configuration | validated `@ConfigurationProperties`, externalized secrets |
| Observability | SLF4J and structured logging instead of console output |

The preferred remediation shape is:

1. keep the hard architectural rule for release gating when the issue is severe;
2. show a `SPR_ALT###` advisor beside it with the concrete Spring alternative;
3. exclude advisor findings from prioritized release-blocking actions while keeping them visible in reports and UI.

### Manual Spring Security checks

Spring Guardian now detects manual authorization signals that should be owned by Spring Security instead of scattered business code:

| ID | Problem | Spring alternative |
|---|---|---|
| `SPR_ALT021_MANUAL_PRINCIPAL_SECURITY_CHECK` | `principal != null`, `getUserPrincipal() != null` or `Authentication != null` used as an authorization boundary | `SecurityFilterChain`, `@PreAuthorize`, `@AuthenticationPrincipal` or a dedicated authorization service |
| `SPR_ALT022_SECURITY_CONTEXT_HOLDER_IN_BUSINESS_CODE` | services/domain code read `SecurityContextHolder` directly | keep security infrastructure in adapters and pass explicit user/permission data to use cases |
| `SPR_ALT023_MANUAL_ROLE_STRING_CHECK` | scattered `ROLE_*` string checks | `@PreAuthorize`, `AuthorizationManager` or a typed domain permission service |

These findings also reduce the Security and Production Readiness maturity scores because authorization ownership is not fully centralized.
