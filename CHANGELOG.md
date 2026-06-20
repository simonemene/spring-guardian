
## UI low-noise Spring Review refinement

- Fixed Spring Findings visibility with robust fallback extraction and clear-filter empty state.
- Simplified Spring Review Plan with only key risks, weak scores, layer map and final upgrade path.
- Reworked Spring Layer Map dialog into a class-level graph when finding evidence is available.
- Added inferred missing-service-boundary warnings when controllers exist without services.
- Reduced wording and clarified Spring Boot Upgrade Path as a final modernization step.

## 1.1.2 - Calibrated Architect Mode UX and security maturity

- Replaced raw Mermaid-first architecture map with a visual Controller -> Service -> Repository -> Entity graph and an HTML/CSS architecture dialog in UI and HTML reports.
- Calibrated Spring Maturity Score across Architecture, Web/API, Security, Persistence, Configuration, Observability, Testing, Production Readiness and Spring Modernity using capabilities, findings, occurrence counts and Spring-specific anti-patterns.
- Added Spring Security alternative rules for manual `Principal`/`Authentication` null checks, `SecurityContextHolder` in business code and scattered `ROLE_*` string checks.
- Reworked Spring Upgrade Path as an explainable roadmap with why, evidence, actions, effort, risk and optional OpenRewrite recipes per step.
- Updated README and Architect Mode documentation with guidance for reading score, map, roadmap, checklist and OpenRewrite suggestions.
- Added regression tests for calibrated maturity scoring, security alternatives, upgrade path details and HTML report dialog rendering.

## 1.1.1 - Architect Mode UX and test hardening

- Fixed Architect Mode UI/report rendering issues: raw browser `<details>` widgets were replaced with styled code panels, upgrade risk badges no longer collide with titles, and Mermaid/OpenRewrite sections are readable.
- Added a visual Architecture Map board in UI/HTML alongside the Mermaid export, so users see dependencies before raw graph code.
- Improved enterprise module inference for packages such as `com.gruppoveronesi.web.dedalo.*`, avoiding company-name-only module labels.
- Expanded OpenRewrite suggestions export with additional guarded recipes for Spring Boot modernization, Jakarta migration, configuration properties, dependency governance and Security DSL modernization.
- Added frontend downloads for Architect Mode artifacts: checklist JSON, modernization plan Markdown, module map Mermaid and OpenRewrite YAML.
- Added Production Readiness Advisor detail panel to the UI.
- Added more regression tests for architecture map precision, cycles, production readiness, upgrade path, OpenRewrite export, Architect Mode HTML UX and CLI JSON output.
- Updated Spring server integration coverage to assert `architectMode` is returned by API scan responses.


## 1.1.0 - Architect Mode MVP

- Added Spring Guardian Architect Mode as deterministic roadmap engine: Spring Maturity Score, Spring Architecture Map, Modernization Plan, Production Readiness Advisor, Spring Upgrade Path and OpenRewrite suggestions export.
- Added `spring-guardian architect` CLI with `--format text|json|html|markdown`, `--export-checklist`, `--export-mermaid` and `--export-openrewrite`.
- Extended `ArchitectureReviewReport` with `architectMode` while preserving backward-compatible constructors.
- Added Mermaid architecture graph export and basic package/module cycle detection.
- Added exportable modernization checklist items with files, suggested change, effort, impact and related findings.
- Updated HTML report with Architect Mode dashboard, maturity area scores, module map, cycles, checklist, upgrade path and OpenRewrite YAML section.
- Updated UI navigation: workflow pages remain numbered, Mission and Contacts are now separated as product information.
- Added Architect Mode UI page with maturity score, module map, checklist, Mermaid graph, upgrade path and OpenRewrite suggestions.
- Added tests for Architect Mode core, CLI exports and HTML report rendering.


## 2026-06-19 - Test stability fix

- Fixed `SPR_ALT018_CONFIGURATION_PROPERTIES_WITHOUT_VALIDATION` detection for `@ConfigurationProperties` records/classes by running a deterministic source fallback in addition to JavaParser AST detection.
- Kept backward-compatible report model constructors for `AffectedComponent` and `FindingGroup` so existing tests and consumers compile against the evolved report model.
- Validation performed in sandbox with `javac` on backend main modules and a lightweight JUnit-compatible test harness for `guardian-core` tests: 26/26 core test methods passed. Maven itself is not executable in this sandbox.

# Changelog

## Unreleased - Test compile compatibility fix

- Fixed report model test compilation compatibility by restoring backward-compatible constructors for `AffectedComponent` and `FindingGroup`.
- Added `ReportModelCompatibilityTest` to prevent regressions when the report model evolves.
- No frontend changes.


## Unreleased - test stability fix

- Fixed `SPR_ALT018_CONFIGURATION_PROPERTIES_WITHOUT_VALIDATION` detection for `@ConfigurationProperties` records or sources that JavaParser may not fully parse, by adding a conservative text fallback.
- Keeps the enterprise Spring Alternatives test aligned with the intended rule behavior.


## 1.0.2 - 2026-06-19

### Added
- Unified backend Spring Alternative catalog `SpringAlternativeRulesCatalog`.
- New `SPR_ALT001`-`SPR_ALT020` enterprise Spring Alternatives for Security, Web/API, Actuator, JPA, transactions, configuration and observability.
- New advisor detectors for entity request bodies, repository business logic, concatenated query strings and unvalidated `@ConfigurationProperties`.
- Regression tests for unified catalog IDs and enterprise Spring Alternative detection.

### Changed
- `GuardianRules.defaultRules()` now wires the unified Spring Alternative catalog and deduplicates rules by stable id.
- Canonical legacy advisor ids are preserved for compatibility: `ADV001` remains the ObjectMapper advisor and `ADV003` remains the RestTemplate advisor.
- Report categorization now treats `SPR_ALT###` as Spring Alternative Advisor findings.

### Removed
- Replaced the split `SpringAlternativeAdvisorCatalog` and `WebBatchAdvisorCatalog` with a single unified catalog.

## 1.0.1 - 2026-06-19

### Added
- New core rule `SPR096_JPA_OPEN_IN_VIEW_ENABLED` for explicit `spring.jpa.open-in-view=true` production risk.
- New `guardian-report` module.
- Self-contained HTML report renderer with overview, quality gates, architecture areas, actions, evidence-first findings and Spring Alternatives.
- CLI support for `--format html`.
- Report renderer unit test and CLI HTML output regression test.
- Documentation for CLI usage, reporting architecture, rules, Spring Alternatives and contributor workflow.

### Changed
- CLI now defaults to automatic project type detection (`UNKNOWN`) instead of forcing `WEB_API`.
- CLI text rendering moved out of `guardian-cli` into `guardian-report`.
- Root command version aligned to `1.0.0`.
- `.gitignore` now keeps `package-lock.json` for reproducible UI builds.

### Hardened
- ZIP/folder upload workspace preparation now skips generated/dependency folders such as `.git`, `target`, `node_modules`, `.angular`, `dist` and `out-tsc`.
- Upload extraction now enforces archive entry and uncompressed-size limits.