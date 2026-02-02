import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PkceService {
  private readonly alphabet =
    'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';

  /**
   * Generate a cryptographically random code verifier string.
   * RFC7636 requires a verifier length between 43 and 128 characters.
   */
  generateCodeVerifier(length = 64): string {
    const array = new Uint8Array(length);
    crypto.getRandomValues(array);
    return Array.from(array)
      .map((x) => this.alphabet[x % this.alphabet.length])
      .join('');
  }

  /**
   * Generate the SHA-256 code challenge for the given verifier and return it
   * as a base64-url-encoded string. This is what is sent to the authorization
   * endpoint to bind the authorization code to the verifier.
   */
  async generateCodeChallenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return this.base64UrlEncode(new Uint8Array(digest));
  }

  /**
   * Generate a random state value used to mitigate CSRF attacks during the
   * authorization flow. Stored in session and validated on callback.
   */
  generateState(length = 32): string {
    const array = new Uint8Array(length);
    crypto.getRandomValues(array);
    return this.base64UrlEncode(array);
  }

  /**
   * Helper: base64-url encode a byte array (no padding, + -> -, / -> _).
   */
  private base64UrlEncode(bytes: Uint8Array): string {
    let binary = '';
    bytes.forEach((b) => (binary += String.fromCharCode(b)));
    const base64 = btoa(binary);
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
  }
}
