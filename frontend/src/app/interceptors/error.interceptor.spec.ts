import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { errorInterceptor } from './error.interceptor';
import { ToastService } from '../services/toast.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let toastService: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    toastService = TestBed.inject(ToastService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should intercept 400 with ProblemDetail invalidParams and trigger toast error', () => {
    const errorPayload = {
      type: 'https://api.gestionprojets.tn/errors/validation',
      title: 'Erreur de Validation',
      status: 400,
      detail: 'Échec de validation des données d\'entrée.',
      invalidParams: {
        nom: 'Le nom de l\'entreprise est obligatoire.'
      }
    };

    let errorReceived = false;
    http.post('/api/entreprise/add', {}).subscribe({
      next: () => {
        throw new Error('should have failed with 400');
      },
      error: (err) => {
        errorReceived = true;
        expect(err.status).toBe(400);
      }
    });

    const req = httpMock.expectOne('/api/entreprise/add');
    req.flush(errorPayload, { status: 400, statusText: 'Bad Request' });

    expect(errorReceived).toBe(true);
    const toasts = toastService.toasts();
    expect(toasts.length).toBe(1);
    expect(toasts[0].type).toBe('error');
    expect(toasts[0].title).toBe('Erreur de Validation');
    expect(toasts[0].message).toContain('nom: Le nom de l\'entreprise est obligatoire.');
  });

  it('should handle network error (status 0)', () => {
    let errorReceived = false;
    http.get('/api/entreprise/all').subscribe({
      next: () => {
        throw new Error('should have failed with 0');
      },
      error: (err) => {
        errorReceived = true;
        expect(err.status).toBe(0);
      }
    });

    const req = httpMock.expectOne('/api/entreprise/all');
    req.error(new ProgressEvent('error'));

    expect(errorReceived).toBe(true);
    const toasts = toastService.toasts();
    expect(toasts.length).toBe(1);
    expect(toasts[0].title).toBe('Erreur Réseau');
  });
});
