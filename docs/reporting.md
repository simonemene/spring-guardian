# Reporting Architecture

`guardian-report` centralizes report rendering outside the CLI.

## Modules

- `guardian-core` produces `ArchitectureReviewReport`.
- `guardian-report` renders text and self-contained HTML.
- `guardian-cli` handles command-line parsing, JSON serialization and exit codes.
- `guardian-server` exposes API scans and returns structured JSON for the UI.

## HTML report

The HTML report is self-contained and designed for enterprise review sessions:

- executive summary and architecture score;
- severity distribution;
- release readiness and quality gates;
- architecture areas;
- recommended actions;
- evidence-first finding cards;
- highlighted Spring Alternatives with official documentation links.

The renderer escapes user/project content before writing HTML.
