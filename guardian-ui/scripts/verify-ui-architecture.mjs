import { existsSync, readFileSync } from 'node:fs';

const requiredFiles = [
  'src/app/app.routes.ts',
  'src/app/core/report-state.service.ts',
  'src/app/pages/dashboard/dashboard-page.component.ts',
  'src/app/pages/architect-mode/architect-mode.component.ts',
  'src/app/pages/architect-mode/architecture-graph-dialog/architecture-graph-dialog.component.ts',
  'src/app/pages/checklist/improvement-checklist.component.ts',
  'src/app/pages/findings/findings-page.component.ts',
  'src/app/pages/alternatives/alternatives-page.component.ts',
  'src/app/pages/quality/quality-page.component.ts',
  'src/app/pages/json-view/json-view.component.ts',
];

const missing = requiredFiles.filter((file) => !existsSync(file));
if (missing.length > 0) {
  console.error(`Missing modular UI files:\n${missing.join('\n')}`);
  process.exit(1);
}

const read = (file) => readFileSync(file, 'utf8');

const appTs = read('src/app/app.component.ts');
const appHtml = read('src/app/app.component.html');
if (appTs.length > 11500) {
  console.error('AppComponent is growing again. Keep it as shell/layout only.');
  process.exit(1);
}
for (const expected of ['Spring Overview', 'Spring Findings', 'Spring Alternatives', 'Spring Quality Gates', 'Spring Review Plan', 'Spring Modernization Checklist', 'Spring Exports']) {
  if (!appTs.includes(expected) && !appHtml.includes(expected)) {
    console.error(`Missing Spring navigation label: ${expected}`);
    process.exit(1);
  }
}
if (appTs.includes("route: '/risk-inbox'")) {
  console.error('Risk Inbox must be merged into Spring Review Plan, not exposed as a separate workflow page.');
  process.exit(1);
}

const routes = read('src/app/app.routes.ts');
if (!routes.includes('loadComponent')) {
  console.error('Routes must use lazy loaded standalone pages with loadComponent.');
  process.exit(1);
}
if (!routes.includes("path: 'risk-inbox', redirectTo: 'architect'")) {
  console.error('Old risk-inbox route must redirect to the unified Spring Review Plan.');
  process.exit(1);
}

const state = read('src/app/core/report-state.service.ts');
for (const expected of ['text(it: string, en: string)', 'downloadExecutiveSummaryMarkdown', 'downloadChecklistJson', 'importChecklistState', 'globalSearchResults']) {
  if (!state.includes(expected)) {
    console.error(`State behavior missing: ${expected}`);
    process.exit(1);
  }
}

const dashboard = read('src/app/pages/dashboard/dashboard-page.component.html') + read('src/app/pages/dashboard/dashboard-page.component.ts');
for (const expected of ['Primo impatto', 'Diagnosi rapida', 'Top Spring findings', 'Aree Spring deboli']) {
  if (!dashboard.includes(expected)) {
    console.error(`Dashboard must be low-noise and first-impact. Missing: ${expected}`);
    process.exit(1);
  }
}
if (dashboard.includes('Architect Mode')) {
  console.error('Dashboard should use Spring Review Plan wording, not Architect Mode.');
  process.exit(1);
}

const architect = read('src/app/pages/architect-mode/architect-mode.component.html') + read('src/app/pages/architect-mode/architect-mode.component.ts');
for (const expected of ['Spring Review Plan', 'Primo impatto', 'keyRiskCards', 'Spring Maturity Score', 'Spring Layer Map', 'Spring Boot Production Readiness', 'Spring Boot Upgrade Path', 'Roadmap minima']) {
  if (!architect.includes(expected)) {
    console.error(`Unified Spring Review Plan missing: ${expected}`);
    process.exit(1);
  }
}
if (architect.includes('architect-subnav') || architect.includes('Spring Risk Inbox')) {
  console.error('Spring Review Plan must be concise and must not expose noisy subnav/Risk Inbox wording.');
  process.exit(1);
}

const alternatives = read('src/app/pages/alternatives/alternatives-page.component.ts') + read('src/app/pages/alternatives/alternatives-page.component.html');
for (const expected of ['SpringAlternativeObject', 'SecurityFilterChain + @PreAuthorize', 'DTO + Bean Validation', '@ConfigurationProperties validated', 'RestClient / WebClient', 'Actuator + Micrometer', 'Catalogo degli oggetti Spring da preferire']) {
  if (!alternatives.includes(expected)) {
    console.error(`Spring Alternatives must be an object catalog. Missing: ${expected}`);
    process.exit(1);
  }
}
if (alternatives.includes('Generic findings are intentionally hidden')) {
  console.error('Alternatives copy must be rewritten in clear language, not old noisy text.');
  process.exit(1);
}

const findings = read('src/app/pages/findings/findings-page.component.html') + read('src/app/pages/findings/findings-page.component.ts');
for (const expected of ['Pattern Spring', 'Perché conta in Spring', 'Oggetto Spring consigliato', 'finding-drawer']) {
  if (!findings.includes(expected)) {
    console.error(`Spring Findings role is unclear. Missing: ${expected}`);
    process.exit(1);
  }
}

const quality = read('src/app/pages/quality/quality-page.component.ts') + read('src/app/pages/quality/quality-page.component.html');
for (const expected of ['Spring MVC/API boundary', 'Spring Security', 'Spring Data/JPA', 'Spring Boot Production', 'Spring Testing', 'Release Spring readiness']) {
  if (!quality.includes(expected)) {
    console.error(`Spring Quality Gates must be Spring-centric. Missing: ${expected}`);
    process.exit(1);
  }
}

const dialog = read('src/app/pages/architect-mode/architecture-graph-dialog/architecture-graph-dialog.component.html') +
  read('src/app/pages/architect-mode/architecture-graph-dialog/architecture-graph-dialog.component.ts');
for (const expected of ['aria-modal="true"', 'aria-labelledby="architecture-dialog-title"', 'onDialogKeydown']) {
  if (!dialog.includes(expected)) {
    console.error(`Accessible Architecture Map dialog missing: ${expected}`);
    process.exit(1);
  }
}

console.log('Spring low-noise navigation and role separation verification passed.');
