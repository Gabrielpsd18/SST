import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAlertCircle, LucideCheckCircle, LucideInfo, LucideAlertTriangle, LucideX } from '@lucide/angular';
import { NotificationService, Toast } from '../../core/services/notification.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of notificationService.toasts$(); track toast.id) {
        <div [ngClass]="['toast', 'toast--' + toast.type]" 
            (mouseover)="pause(toast.id)"
            (mouseleave)="resume(toast.id)">
          <div class="toast__icon">
            @switch (toast.type) {
              @case ('success') {
                <svg lucideCheckCircle size="20" aria-hidden="true"></svg>
              }
              @case ('error') {
                <svg lucideAlertCircle size="20" aria-hidden="true"></svg>
              }
              @case ('warning') {
                <svg lucideAlertTriangle size="20" aria-hidden="true"></svg>
              }
              @case ('info') {
                <svg lucideInfo size="20" aria-hidden="true"></svg>
              }
            }
          </div>
          <div class="toast__message">{{ toast.message }}</div>
          <button class="toast__close" type="button" (click)="notificationService.removeToast(toast.id)" title="Cerrar">
            <svg lucideX size="16" aria-hidden="true"></svg>
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      bottom: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-width: 400px;
      pointer-events: none;
    }

    .toast {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      backdrop-filter: blur(10px);
      pointer-events: all;
      animation: slideIn 0.3s ease-out;
    }

    .toast--success {
      background-color: #10b981;
      color: white;
    }

    .toast--error {
      background-color: #ef4444;
      color: white;
    }

    .toast--warning {
      background-color: #f59e0b;
      color: white;
    }

    .toast--info {
      background-color: #3b82f6;
      color: white;
    }

    .toast__icon {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .toast__message {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      line-height: 1.4;
    }

    .toast__close {
      background: none;
      border: none;
      color: inherit;
      cursor: pointer;
      padding: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0.7;
      transition: opacity 0.2s;
    }

    .toast__close:hover {
      opacity: 1;
    }

    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    @media (max-width: 640px) {
      .toast-container {
        left: 12px;
        right: 12px;
        max-width: none;
      }

      .toast {
        gap: 10px;
        padding: 14px 12px;
      }

      .toast__message {
        font-size: 13px;
      }
    }
  `]
})
export class ToastContainerComponent {
  protected readonly notificationService = inject(NotificationService);
  protected readonly CheckCircle = LucideCheckCircle;
  protected readonly AlertCircle = LucideAlertCircle;
  protected readonly AlertTriangle = LucideAlertTriangle;
  protected readonly Info = LucideInfo;
  protected readonly X = LucideX;

  pause(toastId: string): void {
    // Implementation for pausing auto-dismiss on hover can be added if needed
  }

  resume(toastId: string): void {
    // Implementation for resuming auto-dismiss can be added if needed
  }
}
