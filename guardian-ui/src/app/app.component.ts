import { CommonModule } from '@angular/common';
import { Component, HostListener, computed, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { ReportStateService } from './core/report-state.service';

interface NavItem {
  label: string;
  labelEn?: string;
  route: string;
  needsReport?: boolean;
  description?: string;
  descriptionEn?: string;
  badge?: string;
}

interface NavGroup {
  title: string;
  titleEn?: string;
  description: string;
  descriptionEn?: string;
  items: NavItem[];
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly currentUrl = signal('/analyze');
  readonly searchOpen = signal(false);
  readonly commandPaletteOpen = signal(false);

  readonly navGroups: NavGroup[] = [
    {
      title: 'Start',
      titleEn: 'Start',
      description: 'Avvio review',
      descriptionEn: 'Start review',
      items: [
        { badge: '01', label: 'Analizza', labelEn: 'Analyze', route: '/analyze', description: 'Carica progetto Spring', descriptionEn: 'Load a Spring project' },
      ],
    },
    {
      title: 'Review',
      titleEn: 'Review',
      description: 'Punti nevralgici',
      descriptionEn: 'Key Spring points',
      items: [
        { badge: '02', label: 'Spring Overview', labelEn: 'Spring Overview', route: '/dashboard', needsReport: true, description: 'Sintesi e priorità', descriptionEn: 'Summary and priorities' },
        { badge: '03', label: 'Spring Findings', labelEn: 'Spring Findings', route: '/findings', needsReport: true, description: 'Problemi e pattern', descriptionEn: 'Issues and patterns' },
        { badge: '04', label: 'Spring Alternatives', labelEn: 'Spring Alternatives', route: '/alternatives', needsReport: true, description: 'Oggetti Spring consigliati', descriptionEn: 'Recommended Spring objects' },
        { badge: '05', label: 'Spring Quality Gates', labelEn: 'Spring Quality Gates', route: '/quality', needsReport: true, description: 'Gate Spring essenziali', descriptionEn: 'Essential Spring gates' },
        { badge: '06', label: 'Spring Review Plan', labelEn: 'Spring Review Plan', route: '/architect', needsReport: true, description: 'Rischi, score e roadmap', descriptionEn: 'Risks, score and roadmap' },
      ],
    },
    {
      title: 'Action plan',
      titleEn: 'Action plan',
      description: 'Piano operativo',
      descriptionEn: 'Execution plan',
      items: [
        { badge: '07', label: 'Spring Modernization Checklist', labelEn: 'Spring Modernization Checklist', route: '/checklist', needsReport: true, description: 'Azioni assegnabili', descriptionEn: 'Assignable actions' },
        { badge: '08', label: 'Spring Exports', labelEn: 'Spring Exports', route: '/export', needsReport: true, description: 'Report e handoff', descriptionEn: 'Reports and handoff' },
      ],
    },
  ];

  readonly info: NavItem[] = [
    { label: 'Missione', labelEn: 'Mission', route: '/mission', description: 'Visione prodotto', descriptionEn: 'Product vision' },
    { label: 'Contatti', labelEn: 'Contacts', route: '/contacts', description: 'Contributi e canali', descriptionEn: 'Contributions and channels' },
  ];

  readonly reviewPath = [
    { label: 'Scan', route: '/analyze' },
    { label: 'Overview', route: '/dashboard' },
    { label: 'Findings', route: '/findings' },
    { label: 'Alternatives', route: '/alternatives' },
    { label: 'Quality', route: '/quality' },
    { label: 'Plan', route: '/architect' },
    { label: 'Checklist', route: '/checklist' },
    { label: 'Exports', route: '/export' },
  ];

  readonly currentPage = computed(() => {
    const route = this.currentUrl().split('?')[0];
    const all = [...this.navGroups.flatMap((group) => group.items), ...this.info];
    return all.find((item) => item.route === route) ?? all[0];
  });

  readonly breadcrumb = computed(() => {
    const route = this.currentUrl().split('?')[0];
    const group = this.navGroups.find((navGroup) => navGroup.items.some((item) => item.route === route));
    if (group) {
      const item = group.items.find((entry) => entry.route === route);
      return `Spring Guardian / ${this.groupTitle(group)} / ${item ? this.label(item) : 'Review'}`;
    }
    const info = this.info.find((entry) => entry.route === route);
    return `Spring Guardian / Project / ${info ? this.label(info) : this.state.text('Analizza', 'Analyze')}`;
  });

  readonly nextBestAction = computed(() => {
    const route = this.currentUrl().split('?')[0];
    if (!this.state.hasReport()) {
      return { label: this.state.text('Avvia Spring scan', 'Start Spring scan'), route: '/analyze' };
    }
    if (route === '/analyze') return { label: this.state.text('Leggi overview', 'Open overview'), route: '/dashboard' };
    if (route === '/dashboard') return { label: this.state.text('Apri finding', 'Open findings'), route: '/findings' };
    if (route === '/findings') return { label: this.state.text('Scegli alternative Spring', 'Choose Spring alternatives'), route: '/alternatives' };
    if (route === '/alternatives') return { label: this.state.text('Controlla quality gates', 'Check quality gates'), route: '/quality' };
    if (route === '/quality') return { label: this.state.text('Apri piano finale', 'Open final plan'), route: '/architect' };
    if (route === '/architect') return { label: this.state.text('Apri checklist', 'Open checklist'), route: '/checklist' };
    if (route === '/checklist') return { label: this.state.text('Esporta piano Spring', 'Export Spring plan'), route: '/export' };
    return { label: this.state.text('Torna alla overview', 'Back to overview'), route: '/dashboard' };
  });


  readonly commandPaletteActions = computed(() => {
    const hasReport = this.state.hasReport();
    const actions = [
      { label: this.state.text('Apri Spring Review Plan', 'Open Spring Review Plan'), hint: this.state.text('Rischi, score e roadmap in una vista unica', 'Risks, score and roadmap in one view'), route: '/architect', enabled: hasReport },
      { label: this.state.text('Filtra finding Security', 'Filter Security findings'), hint: this.state.text('Vai ai finding Spring Security', 'Open Spring Security findings'), route: '/findings?severity=ALL&query=security', enabled: hasReport },
      { label: this.state.text('Filtra finding JPA/Data', 'Filter JPA/Data findings'), hint: this.state.text('Vai ai finding Spring Data/JPA', 'Open Spring Data/JPA findings'), route: '/findings?severity=ALL&query=jpa', enabled: hasReport },
      { label: this.state.text('Apri Spring Checklist', 'Open Spring Checklist'), hint: this.state.text('Piano operativo persistente', 'Persistent action plan'), route: '/checklist', enabled: hasReport },
      { label: this.state.text('Esporta Executive Summary', 'Export Executive Summary'), hint: this.state.text('Markdown per review', 'Markdown for review'), route: '/export', enabled: hasReport },
      { label: this.state.text('Avvia Spring scan', 'Start Spring scan'), hint: this.state.text('Carica ZIP/cartella o path locale', 'Upload ZIP/folder or local path'), route: '/analyze', enabled: true },
    ];
    const query = this.state.globalQuery().trim().toLowerCase();
    const filtered = query.length < 2 ? actions : actions.filter((action) => `${action.label} ${action.hint}`.toLowerCase().includes(query));
    return filtered;
  });

  constructor(readonly state: ReportStateService, private readonly router: Router) {
    this.currentUrl.set(this.router.url);
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.currentUrl.set(event.urlAfterRedirects);
        this.searchOpen.set(false);
      });
  }

  label(item: NavItem): string {
    return this.state.text(item.label, item.labelEn ?? item.label);
  }

  description(item: NavItem): string {
    return this.state.text(item.description ?? '', item.descriptionEn ?? item.description ?? '');
  }

  groupTitle(group: NavGroup): string {
    return this.state.text(group.title, group.titleEn ?? group.title);
  }

  groupDescription(group: NavGroup): string {
    return this.state.text(group.description, group.descriptionEn ?? group.description);
  }

  pathState(route: string): 'done' | 'active' | 'todo' {
    const ordered = this.reviewPath.map((step) => step.route);
    const currentIndex = ordered.indexOf(this.currentUrl().split('?')[0]);
    const routeIndex = ordered.indexOf(route);
    if (routeIndex === currentIndex) return 'active';
    if (this.state.hasReport() && routeIndex >= 0 && routeIndex < currentIndex) return 'done';
    if (route === '/analyze' && this.state.hasReport()) return 'done';
    return 'todo';
  }

  performSearch(value: string): void {
    this.state.setGlobalQuery(value);
    this.searchOpen.set(value.trim().length >= 2);
  }

  closeSearch(): void {
    this.searchOpen.set(false);
  }

  openCommandPalette(): void {
    this.commandPaletteOpen.set(true);
    this.searchOpen.set(false);
  }

  closeCommandPalette(): void {
    this.commandPaletteOpen.set(false);
  }

  runCommand(route: string): void {
    this.commandPaletteOpen.set(false);
    this.router.navigateByUrl(route);
  }

  @HostListener('document:keydown', ['$event'])
  onKeyboardShortcut(event: KeyboardEvent): void {
    const key = event.key.toLowerCase();
    if ((event.ctrlKey || event.metaKey) && key === 'k') {
      event.preventDefault();
      this.openCommandPalette();
    }
    if (event.key === 'Escape') {
      this.commandPaletteOpen.set(false);
      this.searchOpen.set(false);
    }
  }
}
