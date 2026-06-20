import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { ScoreBadgeComponent } from '../../shared/score-badge/score-badge.component';
import { ArchitectureGraphDialogComponent } from './architecture-graph-dialog/architecture-graph-dialog.component';
import {
  ArchitectModeReport,
  ProductionReadinessReport,
  SpringMaturityAreaScore,
  FindingGroup,
  UpgradeStep,
} from '../../spring-guardian.model';

interface ReadinessCheck {
  label: string;
  status: 'ready' | 'risky' | 'missing';
  description: string;
}

interface ScoreAdjustment {
  label: string;
  points: number;
}

interface ScoreAudit {
  baseline: number;
  penalties: ScoreAdjustment[];
  bonuses: ScoreAdjustment[];
  finalScore: number;
}

@Component({
  selector: 'sg-architect-mode',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent, ScoreBadgeComponent, ArchitectureGraphDialogComponent],
  templateUrl: './architect-mode.component.html',
  styleUrl: './architect-mode.component.scss',
})
export class ArchitectModeComponent {
  readonly graphOpen = signal(false);
  readonly topWeakAreas = computed(() => this.state.architectMode()?.maturityScore.areas
    .filter((area) => area.score < 75)
    .sort((a, b) => a.score - b.score)
    .slice(0, 4) ?? []);

  constructor(readonly state: ReportStateService) {}

  narrative(architect: ArchitectModeReport): string {
    const weak = this.topWeakAreas().map((area) => area.name).join(', ');
    const cycleCount = architect.architectureMap.cycles.length;
    const moduleRisks = architect.architectureMap.modules.reduce((total, module) => total + module.risks.length, 0);
    const production = architect.productionReadiness.score < 60
      ? this.state.text('Readiness produzione prioritaria.', 'Production readiness is a priority.')
      : this.state.text('Readiness discreta, da consolidare prima degli upgrade.', 'Readiness is acceptable, but consolidate it before upgrades.');
    const architecture = cycleCount > 0 || moduleRisks > 0
      ? this.state.text(
          `${moduleRisks} rischi modulo/layer e ${cycleCount} ciclo/i: ripulisci i boundary Spring.`,
          `${moduleRisks} module/layer risks and ${cycleCount} cycle(s): clean up Spring boundaries.`
        )
      : this.state.text('Nessun ciclo forte: usa la checklist per consolidare i layer.', 'No strong cycles: use the checklist to consolidate layers.');
    return this.state.text(
      `Profilo: ${architect.fingerprint.summary}. Aree da leggere per prime: ${weak || 'nessuna area critica evidente'}. ${architecture} ${production}`,
      `Profile: ${architect.fingerprint.summary}. Read first: ${weak || 'no obvious critical area'}. ${architecture} ${production}`
    );
  }

  areaPenaltySummary(area: SpringMaturityAreaScore): string {
    const penalties = this.penalties(area);
    const recommendations = area.recommendations?.length ? area.recommendations.join(' · ') : this.state.text('Mantieni questa area monitorata.', 'Keep this area monitored.');
    return `${penalties.join(' · ') || this.state.text('Nessuna penalità specifica rilevante.', 'No specific relevant penalty.')} — ${recommendations}`;
  }

  penalties(area: SpringMaturityAreaScore): string[] {
    return area.drivers?.length ? area.drivers : [this.state.text('Nessuna penalità specifica rilevante.', 'No specific relevant penalty.')];
  }

  bonuses(area: SpringMaturityAreaScore): string[] {
    const score = area.score;
    const bonuses: string[] = [];
    if (score >= 75) {
      bonuses.push(this.state.text('Area sopra soglia enterprise minima', 'Area above minimum enterprise threshold'));
    }
    if (area.recommendations.length === 0) {
      bonuses.push(this.state.text('Nessuna remediation prioritaria rilevata', 'No priority remediation detected'));
    }
    if (score >= 90) {
      bonuses.push(this.state.text('Pratica Spring-native coerente con lo standard target', 'Spring-native practice aligned with target standard'));
    }
    return bonuses.length ? bonuses : [this.state.text('Bonus non disponibili: completa le remediation per migliorare lo score.', 'No bonus available: complete remediation to improve the score.')];
  }

  scoreTone(score: number): 'bad' | 'warn' | 'good' {
    if (score < 55) return 'bad';
    if (score < 75) return 'warn';
    return 'good';
  }

  scoreAudit(area: SpringMaturityAreaScore): ScoreAudit {
    const drivers = area.drivers?.length ? area.drivers : [];
    const recommendations = area.recommendations?.length ? area.recommendations : [];
    const targetDrop = Math.max(0, 100 - area.score);
    const driverWeight = drivers.length > 0 ? Math.max(6, Math.round(targetDrop / Math.max(drivers.length, 1))) : 0;
    const penalties = drivers.map((driver, index) => ({
      label: driver,
      points: index === drivers.length - 1
        ? Math.max(1, targetDrop - driverWeight * (drivers.length - 1))
        : driverWeight,
    })).filter((item) => item.points > 0);

    if (penalties.length === 0 && area.score < 100) {
      penalties.push({ label: 'Capability Spring o finding rilevati dal backend hanno ridotto lo score area.', points: targetDrop });
    }

    const bonuses: ScoreAdjustment[] = [];
    if (area.score >= 70) {
      bonuses.push({ label: 'Area sopra soglia minima: le pratiche rilevate sono parzialmente coerenti con lo standard target.', points: 5 });
    }
    if (recommendations.length === 0) {
      bonuses.push({ label: 'Nessuna remediation prioritaria proposta per questa area.', points: 5 });
    }
    if (area.score >= 90) {
      bonuses.push({ label: 'Segnale di maturità Spring-native elevato.', points: 10 });
    }

    return {
      baseline: 100,
      penalties,
      bonuses: bonuses.length ? bonuses : [{ label: 'Nessun bonus esplicito: completa le remediation per aumentare lo score.', points: 0 }],
      finalScore: area.score,
    };
  }

  areaAnchor(area: SpringMaturityAreaScore): string {
    return `score-${area.code || area.name}`.toLowerCase().replace(/[^a-z0-9]+/g, '-');
  }

  readinessChecks(readiness: ProductionReadinessReport): ReadinessCheck[] {
    return [
      this.readinessCheck(readiness, 'Actuator sicuro', ['actuator', 'endpoint'], 'Endpoint management limitati e protetti.'),
      this.readinessCheck(readiness, 'Health readiness/liveness', ['health', 'readiness', 'liveness'], 'Health probe adatte a runtime e orchestrator.'),
      this.readinessCheck(readiness, 'Metriche e osservabilità', ['metric', 'micrometer', 'observability', 'tracing'], 'Metriche/tracing disponibili per troubleshooting.'),
      this.readinessCheck(readiness, 'Logging strutturato', ['logging', 'structured', 'log'], 'Log leggibili da piattaforme enterprise.'),
      this.readinessCheck(readiness, 'Segreti esternalizzati', ['secret', 'password', 'token'], 'Nessun segreto versionato o hardcoded.'),
      this.readinessCheck(readiness, 'Profili e configurazione', ['profile', 'configuration', 'property'], 'Config separata per ambiente e validata.'),
    ];
  }

  upgradeExplanation(step: UpgradeStep): string {
    return step.whyRecommended || step.description || 'Step consigliato in base al fingerprint Spring e ai finding rilevati.';
  }

  upgradePhase(step: UpgradeStep): string {
    const text = `${step.title} ${step.description}`.toLowerCase();
    if (text.includes('java') || text.includes('jakarta') || text.includes('boot')) return 'Framework baseline';
    if (text.includes('security') || text.includes('authorization')) return 'Security modernization';
    if (text.includes('production') || text.includes('actuator') || text.includes('observability')) return 'Production foundation';
    if (text.includes('api') || text.includes('controller') || text.includes('dto')) return 'API boundary Spring';
    return 'Modernization';
  }


  relatedFindingsForArea(area: SpringMaturityAreaScore): FindingGroup[] {
    const report = this.state.report();
    if (!report) {
      return [];
    }
    const keywords = this.areaKeywords(area);
    return report.findings
      .filter((finding) => {
        const haystack = [
          finding.ruleId,
          finding.category,
          finding.findingType,
          finding.title,
          finding.whyItMatters,
          finding.suggestedFix,
          finding.guidance?.springAlternative ?? '',
        ].join(' ').toLowerCase();
        return keywords.some((keyword) => haystack.includes(keyword));
      })
      .slice(0, 4);
  }

  springExecutiveDiagnosis(architect: ArchitectModeReport): string {
    const weak = this.topWeakAreas().map((area) => area.name).join(', ');
    const firstAction = architect.modernizationPlan.checklist
      .slice()
      .sort((a, b) => a.priority - b.priority)[0]?.title;
    return `Questa non è una review generica: Spring Guardian valuta Spring MVC/API, Security, Data/JPA, Actuator, configuration, testing e modular boundaries. Il progetto risulta ${architect.fingerprint.summary}. Prima leggi ${weak || 'le aree con score più basso'}, poi esegui il primo intervento suggerito: ${firstAction || 'consolida la checklist di modernizzazione Spring'}.`;
  }

  springLayerAction(moduleName: string, risk: string): string {
    const normalized = risk.toLowerCase();
    if (normalized.includes('controller') && normalized.includes('repository')) {
      return `Crea ${this.classify(moduleName)}Service e sposta l'accesso Spring Data repository fuori dal controller.`;
    }
    if (normalized.includes('service layer') || normalized.includes('missing')) {
      return `Introduci un application service ${this.classify(moduleName)}Service come boundary Spring transazionale Spring.`;
    }
    if (normalized.includes('web') || normalized.includes('controller')) {
      return `Rimuovi dipendenze Spring MVC dal service e passa DTO/command indipendenti dal web layer.`;
    }
    return 'Rendi esplicito il boundary Spring Spring con service applicativi, eventi o adapter dedicati.';
  }

  private classify(value: string): string {
    return value
      .split(/[^a-zA-Z0-9]+/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join('') || 'Application';
  }


  moduleSummaryLabel(module: any): string {
    return `${module.controllers} controller · ${module.services} service · ${module.repositories} repository · ${module.entities} entity`;
  }

  moduleHasLayerGap(module: any): boolean {
    return (module.controllers ?? 0) > 0 && (module.services ?? 0) === 0;
  }

  moduleShortAction(module: any): string {
    if (this.moduleHasLayerGap(module)) {
      return this.state.text('Service boundary mancante: crea un @Service prima dei repository.', 'Missing service boundary: create a @Service before repositories.');
    }
    if ((module.risks?.length ?? 0) > 0) {
      return this.state.text('Rischi layer: apri il grafico.', 'Layer risks: open the graph.');
    }
    return this.state.text('Nessun rischio layer evidente.', 'No obvious layer risk.');
  }

  upgradeShortTitle(step: UpgradeStep): string {
    const title = step.title || '';
    return title
      .replace('Adopt modern Spring Boot testing, logging and client APIs', this.state.text('Modernizza API Spring Boot', 'Modernize Spring Boot APIs'))
      .replace('Modernize the API boundary', this.state.text('Modernizza boundary API', 'Modernize API boundary'))
      .replace('Modernize authorization ownership', this.state.text('Modernizza autorizzazione', 'Modernize authorization'))
      .replace('Add production observability', this.state.text('Aggiungi osservabilità', 'Add observability'));
  }

  private areaKeywords(area: SpringMaturityAreaScore): string[] {
    const text = `${area.code} ${area.name}`.toLowerCase();
    if (text.includes('security')) {
      return ['security', 'csrf', 'permitall', 'principal', 'role', 'authentication', 'authorization', 'actuator'];
    }
    if (text.includes('web') || text.includes('api')) {
      return ['controller', 'requestbody', 'rest', 'dto', 'validation', 'openapi', 'problem'];
    }
    if (text.includes('persistence') || text.includes('data') || text.includes('jpa')) {
      return ['jpa', 'repository', 'entity', 'query', 'transactional', 'open-in-view'];
    }
    if (text.includes('configuration')) {
      return ['configuration', 'properties', 'profile', 'secret', 'value'];
    }
    if (text.includes('observability') || text.includes('production')) {
      return ['actuator', 'health', 'logging', 'metric', 'observability', 'management'];
    }
    if (text.includes('testing')) {
      return ['test', 'mock', 'springboottest', 'mockmvc'];
    }
    if (text.includes('modernity') || text.includes('boot')) {
      return ['resttemplate', 'restclient', 'mockbean', 'boot', 'jakarta', 'javax'];
    }
    return ['controller', 'service', 'repository', 'module', 'package', 'layer'];
  }


  keyRiskCards(): { area: string; title: string; count: number; query: string; severity: string }[] {
    const report = this.state.report();
    if (!report) return [];
    const groups = [
      { area: 'Spring Security', query: 'security', keywords: ['security', 'csrf', 'principal', 'role', 'permit'] },
      { area: 'Spring MVC/API', query: 'controller', keywords: ['controller', 'requestbody', 'validation', 'dto', 'openapi', 'problem'] },
      { area: 'Spring Data/JPA', query: 'jpa', keywords: ['jpa', 'repository', 'entity', 'transaction', 'query', 'open-in-view'] },
      { area: 'Spring Boot Production', query: 'actuator', keywords: ['actuator', 'health', 'logging', 'profile', 'secret', 'observability'] },
      { area: 'Spring Boot Upgrade', query: 'upgrade', keywords: ['upgrade', 'jakarta', 'javax', 'resttemplate', 'mockbean', 'boot'] },
    ];
    return groups.map((group) => {
      const findings = report.findings.filter((finding) => {
        const text = `${finding.ruleId} ${finding.title} ${finding.category} ${finding.suggestedFix} ${finding.guidance?.springAlternative ?? ''}`.toLowerCase();
        return group.keywords.some((keyword) => text.includes(keyword));
      });
      const high = findings.some((finding) => finding.severity === 'CRITICAL' || finding.severity === 'MAJOR');
      return {
        area: group.area,
        title: this.riskTitle(group.area),
        count: findings.length,
        query: group.query,
        severity: high ? this.state.text('Priorità alta', 'High priority') : findings.length > 0 ? this.state.text('Da verificare', 'Review') : this.state.text('Monitor', 'Monitor'),
      };
    }).filter((risk) => risk.count > 0).slice(0, 5);
  }

  private riskTitle(area: string): string {
    switch (area) {
      case 'Spring Security':
        return this.state.text('Autorizzazione, CSRF o Principal manuali', 'Authorization, CSRF or manual Principal checks');
      case 'Spring MVC/API':
        return this.state.text('DTO, validation o boundary controller', 'DTO, validation or controller boundaries');
      case 'Spring Data/JPA':
        return this.state.text('Repository, entity o transazioni', 'Repositories, entities or transactions');
      case 'Spring Boot Production':
        return this.state.text('Actuator, logging, profili o segreti', 'Actuator, logging, profiles or secrets');
      default:
        return this.state.text('Baseline Java/Spring Boot da preparare', 'Java/Spring Boot baseline to prepare');
    }
  }

  roadmapSteps(): { title: string; hint: string; route: string }[] {
    return [
      { title: this.state.text('1. Correggi i finding alti', '1. Fix high findings'), hint: this.state.text('Security, API e Data prima.', 'Security, API and Data first.'), route: '/findings' },
      { title: this.state.text('2. Applica alternative Spring', '2. Apply Spring alternatives'), hint: this.state.text('Scegli gli oggetti Spring giusti.', 'Pick the right Spring objects.'), route: '/alternatives' },
      { title: this.state.text('3. Chiudi checklist', '3. Close checklist'), hint: this.state.text('Assegna owner e decisione.', 'Assign owner and decision.'), route: '/checklist' },
    ];
  }

  private readinessCheck(readiness: ProductionReadinessReport, label: string, keywords: string[], description: string): ReadinessCheck {
    const risk = readiness.risks.find((item) => this.includesAny(item, keywords));
    if (risk) {
      return { label, status: 'risky', description: risk };
    }
    const strength = readiness.strengths.find((item) => this.includesAny(item, keywords));
    if (strength) {
      return { label, status: 'ready', description: strength };
    }
    return { label, status: 'missing', description };
  }

  private includesAny(value: string, keywords: string[]): boolean {
    const normalized = value.toLowerCase();
    return keywords.some((keyword) => normalized.includes(keyword));
  }
}
