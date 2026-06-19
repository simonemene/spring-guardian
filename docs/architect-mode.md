# Spring Guardian Architect Mode

Architect Mode turns a normal Spring Guardian scan into an architectural modernization roadmap.

It is deterministic, local-first and CI-friendly: it does not call AI services and it does not modify source code.

## What it generates

- Spring Project Fingerprint
- Spring Maturity Score
- Spring Architecture Map
- Basic package/module cycle detection
- Modernization Plan
- Exportable Improvement Checklist
- Production Readiness Advisor
- Spring Upgrade Path
- OpenRewrite suggestions YAML

## CLI

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar architect ./my-app --format html --output architecture-report.html
```

Markdown roadmap:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar architect ./my-app --format markdown --output spring-modernization-plan.md
```

Exports:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar architect ./my-app \
  --export-checklist modernization-checklist.json \
  --export-mermaid module-map.mmd \
  --export-openrewrite openrewrite-suggestions.yml
```

## How the Maturity Score is calibrated

The score is not a generic code-quality metric. It is derived from:

- detected Spring capabilities, for example Web, Security, JPA, Actuator, Validation, OpenAPI and Batch
- concrete rule findings and their severity/occurrences
- Spring-specific anti-patterns, for example manual Principal checks, `SecurityContextHolder` in business code, entity exposure, OSIV, unsafe actuator exposure and missing observability

Each area contains drivers and recommendations so the score is explainable.

## Maturity Score areas

- Architecture
- Web/API
- Security
- Persistence
- Configuration
- Observability
- Testing
- Production Readiness
- Spring Modernity

## Architecture Map

The first implementation infers logical modules from package names and Spring stereotypes, counts controllers/services/repositories/entities/clients/events and extracts cross-module imports.

The report and UI render a human-readable architecture dialog with layer cards and arrows. The default flow is:

```text
Controller -> Service -> Repository -> Entity
```

Violations are highlighted when the scanner sees controller-to-repository access, service dependencies on web/controller packages, missing service/application boundaries, cross-module repository access or cycles.

The Mermaid graph remains available as a technical export for documentation and CI artifacts.

## Checklist

Checklist items are exportable JSON and include:

- title
- related files/classes
- severity
- effort
- business impact
- suggested change
- Spring alternative
- related findings


## UI/HTML UX notes

Architect Mode now shows both:

- a readable dependency board for developers and architects
- an exportable Mermaid graph for documentation and diagrams

The UI can export:

- `modernization-checklist.json`
- `spring-modernization-plan.md`
- `module-map.mmd`
- `openrewrite-suggestions.yml`

OpenRewrite suggestions are intentionally generated as suggestions only. Spring Guardian does not modify code automatically.

## Enterprise package inference

Packages such as `com.gruppoveronesi.web.dedalo.service` are mapped to the logical module `dedalo` instead of the company namespace, so architecture maps remain actionable.


## Spring Upgrade Path

The upgrade path is a roadmap, not only a list of versions. Every step contains:

- why the step is recommended
- evidence from findings or detected baseline
- concrete actions to perform
- risk and effort
- Spring alternative
- optional OpenRewrite recipes

OpenRewrite export is suggestion-only. Spring Guardian never changes source code automatically.
