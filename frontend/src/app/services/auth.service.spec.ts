import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthRequestDTO, AuthResponseDTO, RegisterRequestDTO } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created and start unauthenticated', () => {
    expect(service).toBeTruthy();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.isAdmin()).toBe(false);
    expect(service.getToken()).toBeNull();
  });

  it('login should authenticate user, store token and update signals', () => {
    const credentials: AuthRequestDTO = { username: 'admin', password: 'Admin123!' };
    const mockResponse: AuthResponseDTO = {
      token: 'jwt.token.admin',
      type: 'Bearer',
      username: 'admin',
      roles: ['ROLE_ADMIN', 'ROLE_USER']
    };

    service.login(credentials).subscribe((res) => {
      expect(res.token).toBe('jwt.token.admin');
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/auth/login'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);
    req.flush(mockResponse);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.isAdmin()).toBe(true);
    expect(service.currentUser()?.username).toBe('admin');
    expect(service.getToken()).toBe('jwt.token.admin');
  });

  it('register should register new user and set session', () => {
    const regData: RegisterRequestDTO = {
      username: 'newuser',
      email: 'newuser@test.tn',
      password: 'Password123!'
    };
    const mockResponse: AuthResponseDTO = {
      token: 'jwt.token.user',
      type: 'Bearer',
      username: 'newuser',
      roles: ['ROLE_USER']
    };

    service.register(regData).subscribe((res) => {
      expect(res.token).toBe('jwt.token.user');
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/auth/register'));
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.isAdmin()).toBe(false);
    expect(service.currentUser()?.username).toBe('newuser');
  });

  it('logout should clear currentUser and localStorage', () => {
    const mockResponse: AuthResponseDTO = {
      token: 'jwt.token.admin',
      type: 'Bearer',
      username: 'admin',
      roles: ['ROLE_ADMIN']
    };

    service.login({ username: 'admin', password: 'pwd' }).subscribe();
    const req = httpMock.expectOne((r) => r.url.endsWith('/auth/login'));
    req.flush(mockResponse);

    expect(service.isAuthenticated()).toBe(true);

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.currentUser()).toBeNull();
    expect(service.getToken()).toBeNull();
  });
});
