import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { ReportStateService } from '../../core/report-state.service';

@Component({
  selector: 'sg-contact-page',
  standalone: true,
  imports: [PageHeaderComponent],
  templateUrl: './contact-page.component.html',
})
export class ContactPageComponent {
  constructor(readonly state: ReportStateService) {}
}
