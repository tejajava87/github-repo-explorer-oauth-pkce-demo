import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  host: { class: 'app-root' },
  template: `
    <div class="app-wrapper">
      <header class="app-header">
        <h1>{{ title() }}</h1>
      </header>
      <main role="main" aria-live="polite" class="app-main">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styleUrls: ['./app.component.scss'],
  imports: [CommonModule, RouterOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppComponent {
  readonly title = signal('GitHub Repo Explorer');
}
