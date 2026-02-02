import { Component, OnInit, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { GithubApiService } from '../../../core/services/github-api.service';
import { RepoSummary } from '../../../core/models/repo-summary';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-repo-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './repo-list.component.html',
  styleUrls: ['./repo-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RepoListComponent implements OnInit {

  // Signals for local component state
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly repos = signal<RepoSummary[]>([]);
  readonly q = signal('');

  readonly filtered = computed(() => {
    const term = this.q().trim().toLowerCase();
    if (!term) return this.repos();

    return this.repos().filter(r => r.full_name.toLowerCase().includes(term));
  });

  constructor(
    private api: GithubApiService,
    private router: Router,
    private auth: AuthService
  ) {}

  /**
   * Component init lifecycle method. Loads repositories from the backend and
   * populates the local signal state. Errors are displayed to the user.
   */
  ngOnInit(): void {
    this.loading.set(true);
    this.error.set(null);

    this.api.getRepos()
      .pipe(
        // guarantees loading is always cleared
        finalize(() => {
          this.loading.set(false);
        })
      )
      .subscribe({
        next: (data) => {
          this.repos.set(this.parseRepos(data));
        },
        error: (err) => {
          this.error.set('Unable to load repositories. Please try again.');
        }
      });
  }

  /**
   * Parse API response which might be an array of repos or a wrapper object
   * with `items` or `repos` properties. Returns an array of RepoSummary.
   */
  private parseRepos(data: unknown): RepoSummary[] {
    if (Array.isArray(data)) return data as RepoSummary[];
    if (data && typeof data === 'object') {
      const d = data as any;
      if (Array.isArray(d.items)) return d.items as RepoSummary[];
      if (Array.isArray(d.repos)) return d.repos as RepoSummary[];
    }
    return [];
  }

  /**
   * Open a repository page in a new browser tab using the repo's HTML URL.
   */
  openRepo(repo: RepoSummary): void {
    window.open(repo.html_url, '_blank');
  }

  /**
   * Perform logout via AuthService and navigate back to the login page.
   */
  logout(): void {
    this.auth.logout().subscribe(() => this.router.navigate(['/login']));
  }
}

