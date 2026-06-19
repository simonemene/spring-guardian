# Rule Catalog

Spring Guardian rules are deterministic and Spring-centric.

## Main families

- `SPR###` core Spring architecture, Web/API, Security, Data, configuration and production-readiness rules.
- `ARCH###` layering, DDD, hexagonal and bounded-context checks.
- `CAP###` missing Spring capability checks such as Validation, OpenAPI and Actuator.
- `ADV###` Spring Alternative Advisor checks for manual Java/low-level patterns.

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
