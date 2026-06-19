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

The Mermaid graph can be rendered by any Mermaid-compatible tool.

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

