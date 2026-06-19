# Changelog

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
