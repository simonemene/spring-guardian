import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'analyze' },
  {
    path: 'analyze',
    loadComponent: () => import('./pages/analyze/analyze-page.component').then((m) => m.AnalyzePageComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard-page.component').then((m) => m.DashboardPageComponent),
  },
  {
    path: 'architect',
    loadComponent: () => import('./pages/architect-mode/architect-mode.component').then((m) => m.ArchitectModeComponent),
  },
  { path: 'risk-inbox', redirectTo: 'architect' },
  {
    path: 'findings',
    loadComponent: () => import('./pages/findings/findings-page.component').then((m) => m.FindingsPageComponent),
  },
  {
    path: 'alternatives',
    loadComponent: () => import('./pages/alternatives/alternatives-page.component').then((m) => m.AlternativesPageComponent),
  },
  {
    path: 'quality',
    loadComponent: () => import('./pages/quality/quality-page.component').then((m) => m.QualityPageComponent),
  },
  {
    path: 'checklist',
    loadComponent: () => import('./pages/checklist/improvement-checklist.component').then((m) => m.ImprovementChecklistComponent),
  },
  {
    path: 'export',
    loadComponent: () => import('./pages/json-view/json-view.component').then((m) => m.JsonViewComponent),
  },
  { path: 'json', redirectTo: 'export' },
  {
    path: 'mission',
    loadComponent: () => import('./pages/mission/mission-page.component').then((m) => m.MissionPageComponent),
  },
  {
    path: 'contacts',
    loadComponent: () => import('./pages/contact/contact-page.component').then((m) => m.ContactPageComponent),
  },
  { path: '**', redirectTo: 'analyze' },
];
