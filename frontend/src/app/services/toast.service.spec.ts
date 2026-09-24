import { TestBed } from '@angular/core/testing';
import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a success toast', () => {
    service.success('Titre test', 'Message test');
    const toasts = service.toasts();
    expect(toasts.length).toBe(1);
    expect(toasts[0].type).toBe('success');
    expect(toasts[0].title).toBe('Titre test');
    expect(toasts[0].message).toBe('Message test');
  });

  it('should add an error toast', () => {
    service.error('Erreur', 'Détail de l\'erreur');
    const toasts = service.toasts();
    expect(toasts.length).toBe(1);
    expect(toasts[0].type).toBe('error');
    expect(toasts[0].title).toBe('Erreur');
  });

  it('should dismiss a toast by id', () => {
    const id = service.info('Info', 'Test');
    expect(service.toasts().length).toBe(1);
    service.dismiss(id);
    expect(service.toasts().length).toBe(0);
  });

  it('should clear all toasts', () => {
    service.info('1');
    service.warning('2');
    expect(service.toasts().length).toBe(2);
    service.clear();
    expect(service.toasts().length).toBe(0);
  });
});
