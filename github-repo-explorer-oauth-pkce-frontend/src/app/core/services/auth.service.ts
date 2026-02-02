import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of, tap, catchError } from 'rxjs';

export interface AuthExchangeRequest {
  code: string;
  codeVerifier: string;
  redirectUri: string;
}

export interface AuthMeResponse {
  authenticated: boolean;
  login?: string;
  name?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = environment.apiBaseUrl;
  private _authenticated = false;

  constructor(private http: HttpClient) {}

  /**
   * Get local cached authentication state.
   */
  get authenticated(): boolean {
    return this._authenticated;
  }

  /**
   * POST /api/auth/exchange with the code, verifier and redirectUri. Backend
   * performs the token exchange (so client secret stays on server) and sets
   * an HttpOnly session cookie. On success, set local authenticated flag to true.
   */
  exchangeCode(req: AuthExchangeRequest): Observable<void> {
    // Backend should set HttpOnly session cookie
    return this.http.post<void>(`${this.base}/api/auth/exchange`, req, {
      withCredentials: true
    }).pipe(
      tap(() => (this._authenticated = true))
    );
  }

  /**
   * GET /api/auth/userstatus to validate the current session on the server. Updates
   * the local `_authenticated` flag based on the response.
   */
  me(): Observable<AuthMeResponse> {
    return this.http.get<AuthMeResponse>(`${this.base}/api/auth/userstatus`, {
      withCredentials: true
    }).pipe(
      tap((res) => (this._authenticated = !!res?.authenticated)),
      catchError(() => {
        this._authenticated = false;
        return of({ authenticated: false });
      })
    );
  }

  /**
   * POST /api/auth/logout — invalidates the server-side session and clears
   * the local authenticated flag.
   */
  logout(): Observable<void> {
    return this.http.post<void>(`${this.base}/api/auth/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => (this._authenticated = false)),
      catchError(() => {
        this._authenticated = false;
        return of(void 0);
      })
    );
  }
}
