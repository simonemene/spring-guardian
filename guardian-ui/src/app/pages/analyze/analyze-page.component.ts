import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

@Component({
  selector: 'sg-analyze-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './analyze-page.component.html',
  styleUrl: './analyze-page.component.scss',
})
export class AnalyzePageComponent {
  constructor(readonly state: ReportStateService) {}

  onZipSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.state.selectZip(input.files?.[0] ?? null);
  }

  onFolderSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.state.selectFolderFiles(Array.from(input.files ?? []));
  }
}
