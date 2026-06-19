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
