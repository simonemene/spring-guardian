import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { FindingGroup } from '../../spring-guardian.model';

interface SpringAlternativeObject {
  name: string;
  area: string;
  whenUseIt: string;
  avoidWhen: string;
  whyBetter: string;
  docs: string;
  keywords: string[];
  relatedFindings: FindingGroup[];
}

@Component({
  selector: 'sg-alternatives-page',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent],
  templateUrl: './alternatives-page.component.html',
  styleUrl: './alternatives-page.component.scss',
})
export class AlternativesPageComponent {
  readonly query = signal('');
  readonly selectedArea = signal('ALL');

  readonly catalog = computed(() => this.buildCatalog()
    .filter((item) => item.relatedFindings.length > 0)
    .filter((item) => this.selectedArea() === 'ALL' || item.area === this.selectedArea())
    .filter((item) => {
      const q = this.query().trim().toLowerCase();
      if (!q) return true;
      return `${item.name} ${item.area} ${item.whenUseIt} ${item.whyBetter} ${item.relatedFindings.map((finding) => finding.ruleId + ' ' + finding.title).join(' ')}`.toLowerCase().includes(q);
    })
    .slice(0, 16));

  constructor(readonly state: ReportStateService) {}

  areas(): string[] {
    return Array.from(new Set(this.buildCatalog().filter((item) => item.relatedFindings.length > 0).map((item) => item.area))).sort();
  }

  ruleQuery(item: SpringAlternativeObject): string {
    return item.relatedFindings.map((finding) => finding.ruleId).slice(0, 3).join(' ');
  }

  private buildCatalog(): SpringAlternativeObject[] {
    const findings = this.state.report()?.findings ?? [];
    const objects: Omit<SpringAlternativeObject, 'relatedFindings'>[] = [
      {
        name: 'SecurityFilterChain + @PreAuthorize',
        area: 'Spring Security',
        whenUseIt: this.state.text('Autorizzazione HTTP o metodo oggi gestita a mano.', 'HTTP or method authorization is handled manually.'),
        avoidWhen: this.state.text('Non usarlo per regole di dominio complesse senza un service permessi.', 'Do not hide complex domain rules without a permission service.'),
        whyBetter: this.state.text('Centralizza la policy e rimuove controlli fragili tipo Principal != null.', 'Centralizes policy and removes fragile checks such as Principal != null.'),
        docs: 'https://docs.spring.io/spring-security/reference/',
        keywords: ['security', 'csrf', 'permit', 'principal', 'role', 'authentication', 'authorization', '@preauthorize', 'securityfilterchain'],
      },
      {
        name: 'DTO + Bean Validation',
        area: 'Spring MVC/API',
        whenUseIt: this.state.text('Controller espone entity o accetta request non validate.', 'A controller exposes entities or accepts unvalidated requests.'),
        avoidWhen: this.state.text('Evita DTO inutili per API interne banali e stabili.', 'Avoid unnecessary DTOs for trivial and stable internal APIs.'),
        whyBetter: this.state.text('Separa contratto REST, dominio JPA e validazione Spring.', 'Separates REST contract, JPA domain and Spring validation.'),
        docs: 'https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html',
        keywords: ['controller', 'requestbody', 'entity', 'dto', 'validation', '@valid', '@validated'],
      },
      {
        name: '@RestControllerAdvice + ProblemDetail',
        area: 'Spring MVC/API',
        whenUseIt: this.state.text('Le API non hanno errori REST coerenti.', 'REST APIs do not return consistent errors.'),
        avoidWhen: this.state.text('Non trasformare eccezioni tecniche in messaggi pubblici sensibili.', 'Do not expose sensitive technical exceptions.'),
        whyBetter: this.state.text('Rende gli errori prevedibili per client e team.', 'Makes errors predictable for clients and teams.'),
        docs: 'https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html',
        keywords: ['controlleradvice', 'restcontrolleradvice', 'problemdetail', 'exception', 'error'],
      },
      {
        name: '@ConfigurationProperties validated',
        area: 'Spring Boot Configuration',
        whenUseIt: this.state.text('Config sparse con @Value, segreti o proprietà non validate.', 'Configuration is scattered with @Value, secrets or unvalidated properties.'),
        avoidWhen: this.state.text('Evita classi enormi: raggruppa per capability.', 'Avoid huge classes: group by capability.'),
        whyBetter: this.state.text('Config tipizzata, validabile e leggibile in CI.', 'Typed, validated and CI-friendly configuration.'),
        docs: 'https://docs.spring.io/spring-boot/reference/features/external-config.html',
        keywords: ['configurationproperties', '@value', 'property', 'profile', 'secret', 'configuration'],
      },
      {
        name: 'RestClient / WebClient',
        area: 'Spring HTTP Clients',
        whenUseIt: this.state.text('Client HTTP manuali o RestTemplate creati direttamente.', 'Manual HTTP clients or RestTemplate instances are created directly.'),
        avoidWhen: this.state.text('Non usare WebClient se non vuoi introdurre modello reactive.', 'Do not use WebClient if you do not want a reactive model.'),
        whyBetter: this.state.text('Client configurabili come bean con timeout, error handling e test più chiari.', 'Bean-based clients with clear timeout, error handling and testing.'),
        docs: 'https://docs.spring.io/spring-framework/reference/integration/rest-clients.html',
        keywords: ['resttemplate', 'restclient', 'webclient', 'httpclient', 'external api', 'timeout'],
      },
      {
        name: '@Transactional service boundary',
        area: 'Spring Data/JPA',
        whenUseIt: this.state.text('Transazioni su controller/private method o repository usati dal web layer.', 'Transactions are on controllers/private methods or repositories are used by the web layer.'),
        avoidWhen: this.state.text('Non rendere transazionale un job enorme: usa chunking o Batch.', 'Do not wrap huge jobs in one transaction: use chunking or Batch.'),
        whyBetter: this.state.text('Confine transazionale chiaro nel service layer.', 'Clear transaction boundary in the service layer.'),
        docs: 'https://docs.spring.io/spring-framework/reference/data-access/transaction.html',
        keywords: ['transaction', '@transactional', 'repository', 'jpa', 'open-in-view', 'query'],
      },
      {
        name: 'Actuator + Micrometer',
        area: 'Spring Boot Production',
        whenUseIt: this.state.text('Mancano health, metrics o osservabilità produzione.', 'Health, metrics or production observability are missing.'),
        avoidWhen: this.state.text('Non esporre endpoint management senza security.', 'Do not expose management endpoints without security.'),
        whyBetter: this.state.text('Rende l’app osservabile e governabile in produzione.', 'Makes the app observable and governable in production.'),
        docs: 'https://docs.spring.io/spring-boot/reference/actuator/index.html',
        keywords: ['actuator', 'micrometer', 'health', 'metric', 'observability', 'logging', 'management'],
      },
      {
        name: 'Spring Modulith boundaries',
        area: 'Spring Architecture',
        whenUseIt: this.state.text('Package/moduli sono accoppiati o ciclici.', 'Packages/modules are coupled or cyclic.'),
        avoidWhen: this.state.text('Non introdurlo prima di capire i confini funzionali.', 'Do not introduce it before understanding functional boundaries.'),
        whyBetter: this.state.text('Rende espliciti i confini e favorisce eventi applicativi.', 'Makes boundaries explicit and encourages application events.'),
        docs: 'https://docs.spring.io/spring-modulith/reference/',
        keywords: ['module', 'boundary', 'cycle', 'modulith', 'package', 'applicationeventpublisher'],
      },
    ];

    return objects.map((object) => ({
      ...object,
      relatedFindings: this.related(findings, object.keywords),
    }));
  }

  private related(findings: FindingGroup[], keywords: string[]): FindingGroup[] {
    return findings.filter((finding) => {
      const text = [
        finding.ruleId,
        finding.title,
        finding.category,
        finding.whyItMatters,
        finding.suggestedFix,
        finding.guidance?.springAlternative ?? '',
        finding.guidance?.recommendedApproach ?? '',
      ].join(' ').toLowerCase();
      const hasKeyword = keywords.some((keyword) => text.includes(keyword));
      const hasSpringGuidance = Boolean(finding.guidance?.springAlternative?.trim()) &&
        /(spring|@|actuator|micrometer|restclient|webclient|transaction|validation|problemdetail|modulith)/i.test(finding.guidance.springAlternative);
      return hasKeyword && hasSpringGuidance;
    }).slice(0, 8);
  }
}
