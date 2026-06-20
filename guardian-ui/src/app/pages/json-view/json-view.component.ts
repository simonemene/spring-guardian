import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

@Component({
  selector: 'sg-json-view',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent],
  templateUrl: './json-view.component.html',
  styleUrl: './json-view.component.scss',
})
export class JsonViewComponent {
  constructor(readonly state: ReportStateService) {}

  json(): string {
    return JSON.stringify(this.state.report(), null, 2);
  }
}
