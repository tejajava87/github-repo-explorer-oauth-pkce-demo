import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PkceService } from '../../../core/services/pkce.service';
import { StorageService } from '../../../core/services/storage.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  error: string | null = null;

  constructor(
    private pkce: PkceService,
    private storage: StorageService
  ) {}

  /**
   * Trigger the PKCE + OAuth authorization flow.
   * Steps:
   * 1. Create a code_verifier and code_challenge (S256).
   * 2. Generate a random state and store both verifier and state in sessionStorage.
   * 3. Redirect the browser to GitHub's authorization endpoint with the challenge and state.
   * The callback route will validate state, read the stored verifier, and call the backend to exchange the code.
   */
  async loginWithGithub(): Promise<void> {
    try {
      this.error = null;

      const verifier = this.pkce.generateCodeVerifier(64);
      const challenge = await this.pkce.generateCodeChallenge(verifier);
      const state = this.pkce.generateState(32);

      this.storage.setSession('pkce_verifier', verifier);
      this.storage.setSession('oauth_state', state);

      const params = new URLSearchParams({
        client_id: environment.githubClientId,
        redirect_uri: environment.redirectUri,
        scope: environment.githubScopes,
        state,
        code_challenge: challenge,
        code_challenge_method: 'S256'
      });

      window.location.href = `https://github.com/login/oauth/authorize?${params.toString()}`;
    } catch (e) {
      this.error = 'Unable to start login. Please try again.';
    }
  }
}
