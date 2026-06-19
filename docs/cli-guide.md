# Spring Guardian CLI Guide

Spring Guardian is CLI-first and CI-friendly.

## Build

```bash
mvn clean verify
mvn -pl guardian-cli -am package
```

## Scan a project

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./my-spring-app
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./my-spring-app --format json --output report.json
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./my-spring-app --format html --output report.html
```

## CI exit codes

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./my-spring-app --fail-on major
```

Exit code `0` means the scan completed and the selected quality threshold did not fail.
Exit code `2` means the scan completed but at least one finding matched `--fail-on`.
Other non-zero exit codes indicate invalid input, unsupported options or execution errors.

## Formats

| Format | Use case |
|---|---|
| `text` | Developer terminal and CI logs |
| `json` | Pipeline integration, dashboards and custom governance |
| `html` | Navigable report for tech leads, modernization workshops and architecture reviews |
