import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, map, Observable, switchMap, throwError } from 'rxjs';
import { SessionUser } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly sessionKey = 'acme.salary.session';
  readonly user = signal<SessionUser | null>(this.readSession());

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(username: string, password: string): Observable<void> {
    const body = new HttpParams().set('username', username).set('password', password);
    return this.http.post<void>('/api/auth/login', body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).pipe(
      switchMap(() => this.http.get<SessionUser>('/api/auth/session')),
      map(session => {
        sessionStorage.setItem(this.sessionKey, JSON.stringify(session));
        this.user.set(session);
      }),
      catchError(error => throwError(() => error))
    );
  }

  logout(): void {
    this.http.post('/api/auth/logout', {}).subscribe({ complete: () => this.finishLogout(), error: () => this.finishLogout() });
  }

  isAuthenticated(): boolean { return this.user() !== null; }

  hasAnyRole(...roles: string[]): boolean {
    const currentRoles = this.user()?.roles ?? [];
    return roles.some(role => currentRoles.includes(role as SessionUser['roles'][number]));
  }

  private finishLogout(): void {
    sessionStorage.removeItem(this.sessionKey);
    this.user.set(null);
    void this.router.navigate(['/login']);
  }

  private readSession(): SessionUser | null {
    const stored = sessionStorage.getItem(this.sessionKey);
    if (!stored) return null;
    try { return JSON.parse(stored) as SessionUser; } catch { sessionStorage.removeItem(this.sessionKey); return null; }
  }
}