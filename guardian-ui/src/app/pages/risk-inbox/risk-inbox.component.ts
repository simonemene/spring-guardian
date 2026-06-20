import { CommonModule } from '@angular/common';
import { Component, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { FindingGroup, ModernizationChecklistItem } from '../../spring-guardian.model';

interface InboxRisk {
  area: string;
  title: string;
  description: string;
  severity: string;
  route: string;
  query?: string;
  checklistItems: number;
  findings: FindingGroup[];
}

@Component({
  selector: 'sg-risk-inbox',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent],
  templateUrl: './risk-inbox.component.html',
  styleUrl: './risk-inbox.component.scss',
})
export class RiskInboxComponent {
  readonly risks = computed(() => this.buildRisks());

  constructor(readonly state: ReportStateService) {}

  topChecklist(): ModernizationChecklistItem[] {
    return this.state.checklist()
      .slice()
      .sort((a, b) => a.priority - b.priority)
      .slice(0, 5);
  }

  keyRisks(): InboxRisk[] {
    return this.risks().slice(0, 5);
  }

  private buildRisks(): InboxRisk[] {
    const report = this.state.report();
    if (!report) {
      return [];
    }
    const findingGroups = [
      {
        area: 'Spring Security',
        keywords: ['security', 'csrf', 'permit', 'principal', 'role', 'actuator'],
        title: 'Rischi Security e autorizzazione manuale',
        description: 'Controlla configurazioni Spring Security, controlli manuali Principal/role e actuator esposto.',
        route: '/findings',
        query: 'security',
      },
      {
        area: 'Spring MVC/API',
        keywords: ['controller', 'requestbody', 'validation', 'dto', 'openapi', 'advice', 'problem'],
        title: 'Boundary API Spring MVC da consolidare',
        description: 'Priorità a DTO, Bean Validation, ProblemDetail e separazione controller/service.',
        route: '/findings',
        query: 'controller',
      },
      {
        area: 'Spring Data/JPA',
        keywords: ['jpa', 'repository', 'entity', 'transaction', 'query', 'open-in-view'],
        title: 'Rischi persistence e transaction boundary',
        description: 'Verifica accesso repository, entity esposte, query rischiose e confini @Transactional.',
        route: '/findings',
        query: 'jpa',
      },
      {
        area: 'Spring Boot Production',
        keywords: ['actuator', 'health', 'logging', 'profile', 'secret', 'observability', 'production'],
        title: 'Blocker di production readiness',
        description: 'Controlla Actuator, health details, segreti, profili e osservabilità Spring Boot.',
        route: '/architect',
      },
      {
        area: 'Spring Upgrade',
        keywords: ['upgrade', 'jakarta', 'javax', 'resttemplate', 'mockbean', 'boot'],
        title: 'Preparazione Spring Boot Upgrade',
        description: 'Individua blocker per Java baseline, Jakarta migration e modernizzazione API Spring.',
        route: '/architect',
      },
    ];

    const risks = findingGroups.map((group) => {
      const findings = report.findings.filter((finding) => {
        const text = `${finding.ruleId} ${finding.title} ${finding.category} ${finding.suggestedFix} ${finding.guidance?.springAlternative ?? ''}`.toLowerCase();
        return group.keywords.some((keyword) => text.includes(keyword));
      }).slice(0, 6);
      const checklistItems = this.state.checklist().filter((item) => {
        const text = `${item.title} ${item.springAlternative} ${item.suggestedChange} ${item.relatedFindings.join(' ')}`.toLowerCase();
        return group.keywords.some((keyword) => text.includes(keyword));
      }).length;
      const high = findings.some((finding) => finding.severity === 'CRITICAL' || finding.severity === 'MAJOR');
      return {
        area: group.area,
        title: group.title,
        description: group.description,
        severity: high ? 'High attention' : findings.length > 0 ? 'Review' : 'Monitor',
        route: group.route,
        query: group.query,
        checklistItems,
        findings,
      };
    });

    return risks
      .filter((risk) => risk.findings.length > 0 || risk.checklistItems > 0 || risk.area.includes('Production') || risk.area.includes('Upgrade'))
      .sort((a, b) => (b.findings.length + b.checklistItems) - (a.findings.length + a.checklistItems));
  }
}
