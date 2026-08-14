import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration?: number; // ms, 0 = infinite
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private toasts = signal<Toast[]>([]);
  readonly toasts$ = this.toasts.asReadonly();

  private idCounter = 0;

  showSuccess(message: string, duration: number = 4000): void {
    this.show(message, 'success', duration);
  }

  showError(message: string, duration: number = 5000): void {
    this.show(message, 'error', duration);
  }

  showInfo(message: string, duration: number = 4000): void {
    this.show(message, 'info', duration);
  }

  showWarning(message: string, duration: number = 4000): void {
    this.show(message, 'warning', duration);
  }

  private show(message: string, type: Toast['type'], duration: number = 4000): void {
    const id = `toast-${++this.idCounter}`;
    const toast: Toast = { id, message, type, duration };

    // Add toast
    this.toasts.update(current => [...current, toast]);

    // Remove toast after duration (if duration > 0)
    if (duration > 0) {
      setTimeout(() => {
        this.removeToast(id);
      }, duration);
    }
  }

  removeToast(id: string): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }

  clearAll(): void {
    this.toasts.set([]);
  }
}
