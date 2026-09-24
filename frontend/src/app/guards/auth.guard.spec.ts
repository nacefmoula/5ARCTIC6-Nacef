import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authServiceMock: { isAuthenticated: () => boolean };
  let routerMock: { createUrlTree: (commands: unknown[], extras?: unknown) => UrlTree };

  beforeEach(() => {
    authServiceMock = {
      isAuthenticated: () => false
    };

    routerMock = {
      createUrlTree: () => {
        return { toString: () => '/login' } as unknown as UrlTree;
      }
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });
  });

  it('should allow access when user is authenticated', () => {
    authServiceMock.isAuthenticated = () => true;

    const route = {} as ActivatedRouteSnapshot;
    const state = { url: '/entreprises' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));
    expect(result).toBe(true);
  });

  it('should redirect to /login with returnUrl when user is not authenticated', () => {
    authServiceMock.isAuthenticated = () => false;

    const route = {} as ActivatedRouteSnapshot;
    const state = { url: '/equipes' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));
    expect(result).toBeDefined();
    expect(result).not.toBe(true);
  });
});
