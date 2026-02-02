import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RepoSummary } from '../models/repo-summary';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GithubApiService {
  private readonly base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  /**
   * GET /api/repos — retrieve the authenticated user's repositories.
   * The request includes credentials so the server-side session cookie will
   * be sent with the request.
   */
  getRepos(): Observable<RepoSummary[]> {
    return this.http.get<RepoSummary[]>(`${this.base}/api/repos`, {
      withCredentials: true
    });
  }


}
