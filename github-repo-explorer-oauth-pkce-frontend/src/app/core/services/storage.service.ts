import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StorageService {
  /**
   * Store a serializable value in sessionStorage under key. Used for
   * one-time PKCE values (state and code verifier) during the auth flow.
   */
  setSession<T>(key: string, value: T): void {
    sessionStorage.setItem(key, JSON.stringify(value));
  }

  /**
   * Retrieve and parse a value from sessionStorage. Returns null if missing.
   */
  getSession<T>(key: string): T | null {
    const raw = sessionStorage.getItem(key);
    return raw ? (JSON.parse(raw) as T) : null;
  }

  /**
   * Remove a named key from sessionStorage (used to clear PKCE state after callback).
   */
  removeSession(key: string): void {
    sessionStorage.removeItem(key);
  }

  /**
   * Clear entire sessionStorage (not typically used for PKCE but provided).
   */
  clearSession(): void {
    sessionStorage.clear();
  }
}
