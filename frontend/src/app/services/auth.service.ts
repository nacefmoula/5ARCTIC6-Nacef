import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthRequestDTO, AuthResponseDTO, AuthUser, RegisterRequestDTO } from '../models/auth.model';

const STORAGE_KEY = 'devops_auth_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/auth`;

  currentUser = signal<AuthUser | null>(this.getInitialUser());
  isAuthenticated = computed(() => !!this.currentUser());
  isAdmin = computed(() => this.currentUser()?.roles.includes('ROLE_ADMIN') ?? false);

  login(credentials: AuthRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.api}/login`, credentials).pipe(
      tap((res) => this.setSession(res))
    );
  }

  register(data: RegisterRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.api}/register`, data).pipe(
      tap((res) => this.setSession(res))
    );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(STORAGE_KEY);
    }
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null;
  }

  private setSession(authRes: AuthResponseDTO): void {
    const user: AuthUser = {
      username: authRes.username,
      roles: authRes.roles,
      token: authRes.token
    };
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    }
    this.currentUser.set(user);
  }

  private getInitialUser(): AuthUser | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }
    try {
      const item = localStorage.getItem(STORAGE_KEY);
      return item ? JSON.parse(item) : null;
    } catch {
      return null;
    }
  }
}
