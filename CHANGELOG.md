
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