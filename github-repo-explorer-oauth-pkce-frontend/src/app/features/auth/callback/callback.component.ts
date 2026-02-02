import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { StorageService } from '../../../core/services/storage.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-callback',
  imports: [CommonModule],
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.scss']
})
export class CallbackComponent {
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService,
    private storage: StorageService
  ) {}

  /**
   * ngOnInit lifecycle: handle the OAuth redirect callback.
   * Steps:
   * 1. Read `code` and `state` from query params.
   * 2. Validate `state` matches stored state to prevent CSRF.
   * 3. Retrieve the stored PKCE verifier.
   * 4. Exchange code via AuthService.exchangeCode and redirect to /repos on success.
   */
  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');

    const expectedState = this.storage.getSession<string>('oauth_state');
    const verifier = this.storage.getSession<string>('pkce_verifier');

    if (!code || !state) {
      this.fail('Missing authorization response parameters.');
      return;
    }
    if (!expectedState || state !== expectedState) {
      this.fail('Invalid state. Please retry login.');
      return;
    }
    if (!verifier) {
      this.fail('Missing PKCE verifier. Please retry login.');
      return;
    }

    // One-time use cleanup
    this.storage.removeSession('oauth_state');
    this.storage.removeSession('pkce_verifier');

    this.auth.exchangeCode({
      code,
      codeVerifier: verifier,
      redirectUri: environment.redirectUri
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/repos']);
      },
      error: () => this.fail('Token exchange failed. Please login again.')
    });
  }

  /**
   * Set an error message and stop the loading indicator.
   */
  private fail(message: string): void {
    this.loading = false;
    this.error = message;
  }

  /**
   * Navigate back to the login page.
   */
  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
