# Spring Guardian

Tested version: includes unit tests, CLI tests and Spring Boot integration tests.

Local-first Spring Boot architecture validator.

This version contains:

- `guardian-core`: deterministic scanner and Spring-specific rules
- `guardian-cli`: installable CLI powered by Picocli
- `guardian-server`: optional Spring Boot REST API
- `docker-compose.yml`: run the server locally

No AI is required in this version. A future module can add optional Spring AI/Ollama explanations.

## Important policy

`@Autowired` field injection is reported in production code, but intentionally ignored under `src/test`, because Spring/JUnit integration tests often use field injection for `MockMvc`, repositories, services and the Spring test context.

## Tests

Run unit tests:

```bash
mvn clean test
```

Run unit + integration tests:

```bash
mvn clean verify
```

Current test coverage includes:

- core scanner/rule unit tests
- `@Autowired` tolerated in `src/test`
- security matcher rule
- unsafe ddl-auto rule
- CLI JSON output and fail-on behavior
- Spring Boot integration test for `/api/v1/scans/local`

## Build everything

```bash
mvn clean package -DskipTests
```

## Run CLI

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./your-spring-project
```

JSON report:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./your-spring-project \
  --format json \
  --output report.json
```

Fail a pipeline on critical findings:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./your-spring-project \
  --fail-on critical
```

Custom API prefix:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan ./your-spring-project \
  --api-prefix /api/v1
```

## Run server with Docker Compose

```bash
docker compose up --build
```

Health:

```bash
curl http://localhost:8080/api/v1/health
```

Scan ZIP:

```bash
curl -X POST http://localhost:8080/api/v1/scans/upload \
  -F "file=@/absolute/path/to/project.zip"
```

Scan mounted local path:

```bash
mkdir -p sample-projects
cp -R /absolute/path/to/project sample-projects/project
docker compose up --build
curl -X POST http://localhost:8080/api/v1/scans/local \
  -H "Content-Type: application/json" \
  -d '{"path":"/scan/project"}'
```


