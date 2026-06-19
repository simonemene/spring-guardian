# Developer Guide

## Add a rule

1. Implement `SpringRule` in `guardian-core`.
2. Prefer JavaParser evidence over string-only matching for architecture rules.
3. Keep false positives low: ignore imports/comments and generated folders.
4. Register the rule in `GuardianRules`.
5. Add localized text in `RuleTextCatalog` and guidance in `RuleGuidanceCatalog`.
6. Add unit tests with fixture source files.
7. Ensure the finding has actionable Spring remediation.

## Add a renderer

1. Implement `ReportRenderer` in `guardian-report`.
2. Never trust project content: escape output-specific special characters.
3. Keep the renderer deterministic and offline.
4. Add tests with representative `ArchitectureReviewReport` data.

## Local verification

```bash
mvn clean verify
npm --prefix guardian-ui install
npm --prefix guardian-ui run build
```
