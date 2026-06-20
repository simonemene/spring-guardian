import { Component, Input } from '@angular/core';

@Component({
  selector: 'sg-metric-card',
  standalone: true,
  template: `
    <article class="metric-card">
      <span>{{ label }}</span>
      <strong>{{ value }}</strong>
      <small>{{ hint }}</small>
    </article>
  `,
  styleUrl: './metric-card.component.scss'
})
export class MetricCardComponent {
  @Input({ required: true }) label = '';
  @Input({ required: true }) value: string | number = '';
  @Input() hint = '';
}
