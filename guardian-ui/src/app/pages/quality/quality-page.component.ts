import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

interface SpringGateView {
  name: string;
  status: string;
  hint: string;
  query: string;
}

@Component({
  selector: 'sg-quality-page',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent],
  templateUrl: './quality-page.component.html',
  styleUrl: './quality-page.component.scss',
})
export class QualityPageComponent {
  constructor(readonly state: ReportStateService) {}

  springGates(): SpringGateView[] {
    const report = this.state.report();
    if (!report) return [];
    const caps = report.capabilities;
    const hasMajor = (keywords: string[]) => report.findings.some((finding) => {
      const text = `${finding.ruleId} ${finding.title} ${finding.category} ${finding.suggestedFix}`.toLowerCase();
      return (finding.severity === 'CRITICAL' || finding.severity === 'MAJOR') && keywords.some((keyword) => text.includes(keyword));
    });
    return [
      {
        name: 'Spring MVC/API boundary',
        status: hasMajor(['controller', 'requestbody', 'validation', 'entity']) ? 'RISK' : 'OK',
        hint: this.state.text('DTO, validation, error handling e controller leggeri.', 'DTOs, validation, error handling and thin controllers.'),
        query: 'controller',
      },
      {
        name: 'Spring Security',
        status: hasMajor(['security', 'csrf', 'permit', 'principal', 'role']) || !caps.usesSpringSecurity ? 'RISK' : 'OK',
        hint: this.state.text('SecurityFilterChain, method security e actuator protetto.', 'SecurityFilterChain, method security and protected actuator.'),
        query: 'security',
      },
      {
        name: 'Spring Data/JPA',
        status: hasMajor(['jpa', 'repository', 'entity', 'transaction', 'query']) ? 'RISK' : 'OK',
        hint: this.state.text('Repository sotto service, transaction boundary e entity non esposte.', 'Repositories below services, transaction boundaries and no exposed entities.'),
        query: 'jpa',
      },
      {
        name: 'Spring Boot Production',
        status: hasMajor(['actuator', 'health', 'secret', 'logging', 'profile']) || !caps.usesActuator ? 'RISK' : 'OK',
        hint: this.state.text('Actuator, health, logging, profili e segreti esterni.', 'Actuator, health, logging, profiles and external secrets.'),
        query: 'actuator',
      },
      {
        name: 'Spring Testing',
        status: hasMajor(['test', 'mockbean', 'springboottest']) ? 'RISK' : 'OK',
        hint: this.state.text('Slice test, context smoke test e test profile.', 'Slice tests, context smoke tests and test profile.'),
        query: 'test',
      },
    ];
  }
}
