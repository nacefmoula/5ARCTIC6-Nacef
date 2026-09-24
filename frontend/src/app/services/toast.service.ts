import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id: string;
  type: ToastType;
  title: string;
  message?: string;
  duration: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private readonly _toasts = signal<Toast[]>([]);
  readonly toasts = this._toasts.asReadonly();

  show(type: ToastType, title: string, message?: string, duration = 4000): string {
    const id = `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
    const newToast: Toast = { id, type, title, message, duration };

    this._toasts.update(current => [...current, newToast]);

    if (duration > 0) {
      setTimeout(() => this.dismiss(id), duration);
    }
    return id;
  }

  success(title: string, message?: string, duration = 3500): string {
    return this.show('success', title, message, duration);
  }

  error(title: string, message?: string, duration = 6000): string {
    return this.show('error', title, message, duration);
  }

  info(title: string, message?: string, duration = 4000): string {
    return this.show('info', title, message, duration);
  }

  warning(title: string, message?: string, duration = 5000): string {
    return this.show('warning', title, message, duration);
  }

  dismiss(id: string): void {
    this._toasts.update(current => current.filter(t => t.id !== id));
  }

  clear(): void {
    this._toasts.set([]);
  }
}
