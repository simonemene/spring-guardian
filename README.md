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

## Rules included

Initial rules:

- `SPR001_HARDCODED_CONFIG`
- `SPR002_FIELD_INJECTION`
- `SPR003_CONTROLLER_INJECTS_REPOSITORY`
- `SPR004_FAT_CONTROLLER`
- `SPR005_MISSING_SERVICE_LAYER`
- `SPR006_ENTITY_EXPOSED_IN_CONTROLLER`
- `SPR007_SELF_INVOCATION_PROXY`
- `SPR008_INVALID_TRANSACTIONAL_USAGE`
- `SPR009_MANUAL_CONNECTION_MANAGEMENT`
- `SPR010_MISSING_REST_CONTROLLER_ADVICE`
- `SPR011_GENERIC_TRY_CATCH`
- `SPR012_MISSING_TESTS`
- `SPR013_PATHVARIABLE_WITHOUT_NAME`
- `SPR014_API_VERSIONING_MISSING`
- `SPR015_MAVEN_VERSION_AND_PROFILE_POLICY`
- `SPR016_NAMING_CONVENTION`

New rules:

- `SPR017_TRANSACTIONAL_ON_CONTROLLER`
- `SPR018_READONLY_TRANSACTION_MISSING`
- `SPR019_SERVICE_RETURNS_ENTITY`
- `SPR020_OPTIONAL_GET_WITHOUT_GUARD`
- `SPR021_CONSOLE_LOGGING`
- `SPR022_LOGGER_SHOULD_BE_STATIC_FINAL`
- `SPR023_REQUEST_BODY_WITHOUT_VALIDATION`
- `SPR024_REQUEST_DTO_WITHOUT_VALIDATION`
- `SPR025_NULL_RETURN_IN_REPOSITORY_OR_SERVICE`
- `SPR026_HTTP_CLIENT_CREATED_MANUALLY`
- `SPR027_LAYER_DEPENDENCY_VIOLATION`
- `SPR028_PACKAGE_STRUCTURE_INCONSISTENT`
- `SPR029_TOO_MANY_DEPENDENCIES`
- `SPR030_GOD_CLASS`
- `SPR031_COMPLEX_SERVICE_METHOD`
- `SPR032_DUPLICATED_MAVEN_DEPENDENCY`
- `SPR033_SPRING_DEPENDENCY_VERSION_OVERRIDDEN`
- `SPR036_MISSING_ENVIRONMENT_PROFILES`
- `SPR037_POSSIBLE_SECRET_IN_CONFIG`
- `SPR038_DDL_AUTO_UNSAFE`
- `SPR039_ACTUATOR_EXPOSE_ALL`
- `SPR040_CSRF_DISABLED`
- `SPR041_PERMIT_ALL_TOO_BROAD`
- `SPR042_PASSWORD_ENCODER_MISSING`
- `SPR043_TEST_WITHOUT_ASSERTIONS`
- `SPR044_SPRING_BOOT_TEST_OVERUSED`
- `SPR045_MOCKITO_TEST_WITHOUT_VERIFY_OR_ASSERT`

Excluded by choice:

- Maven Wrapper missing
- Dockerfile missing
