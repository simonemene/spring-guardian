import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'sg-score-badge',
  standalone: true,
  imports: [CommonModule],
  template: `<strong class="score-badge" [class.good]="score >= 80" [class.warn]="score >= 55 && score < 80" [class.bad]="score < 55">{{ score }}/100</strong>`,
  styleUrl: './score-badge.component.scss'
})
export class ScoreBadgeComponent {
  @Input({ required: true }) score = 0;
}
