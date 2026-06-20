import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { ReportStateService } from '../../core/report-state.service';

@Component({
  selector: 'sg-mission-page',
  standalone: true,
  imports: [PageHeaderComponent],
  templateUrl: './mission-page.component.html',
})
export class MissionPageComponent {
  constructor(readonly state: ReportStateService) {}
}
