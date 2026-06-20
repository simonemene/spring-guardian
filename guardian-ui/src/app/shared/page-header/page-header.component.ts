import { Component, Input } from '@angular/core';

@Component({
  selector: 'sg-page-header',
  standalone: true,
  template: `
    <header class="page-header">
      <span class="eyebrow">{{ eyebrow }}</span>
      <h1>{{ title }}</h1>
      <p>{{ subtitle }}</p>
    </header>
  `,
  styleUrl: './page-header.component.scss'
})
export class PageHeaderComponent {
  @Input() eyebrow = 'Spring Guardian';
  @Input({ required: true }) title = '';
  @Input() subtitle = '';
}
