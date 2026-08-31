import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  LucideAlertCircle,
  LucideCheckCircle,
  LucideClock3,
  LucideFileBadge2,
  LucideFileText,
  LucideFolderOpen,
  LucideLoader2,
  LucideShieldCheck,
  LucideUpload,
  LucideUsers
} from '@lucide/angular';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../auth/services/auth.service';
import {
  DocumentoGeneral,
  DocumentoPersonal,
  DocumentosService
} from '../../services/documentos.service';

@Component({
  selector: 'app-documentos-main',
  standalone: true,
  imports: [
    CommonModule,
    LucideFileText,
    LucideFolderOpen,
    LucideShieldCheck,
    LucideUsers,
    LucideLoader2,
    LucideCheckCircle,
    LucideClock3,
    LucideAlertCircle,
    LucideFileBadge2,
    LucideUpload,
    FormsModule
  ],
  templateUrl: './documentos-main.component.html',
  styleUrl: './documentos-main.component.scss'
})
export class DocumentosMainComponent implements OnInit {
  private readonly documentosService = inject(DocumentosService);
  private readonly authService = inject(AuthService);

  protected readonly activeTab = signal<'generales' | 'personales'>('generales');
  protected readonly generales = signal<DocumentoGeneral[]>([]);
  protected readonly personales = signal<DocumentoPersonal[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly submitError = signal('');
  protected readonly submitting = signal(false);

  protected readonly userRole = signal(this.authService.getUserRole() ?? 'SUPERVISOR');
  protected readonly isAdmin = computed(() => this.userRole() === 'ADMINISTRADOR');
  protected readonly canUploadGeneral = computed(() => this.isAdmin());
  protected readonly canUploadPersonal = computed(() => this.userRole() === 'ADMINISTRADOR' || this.userRole() === 'SUPERVISOR');

  protected generalForm = {
    title: '',
    category: 'NORMATIVA',
    description: '',
    version: '1.0',
    file: null as File | null
  };

  protected personalForm = {
    tipo: 'CERTIFICADO_MEDICO',
    issueDate: '',
    expirationDate: '',
    userId: '',
    file: null as File | null
  };

  protected readonly totalGenerales = computed(() => this.generales().length);
  protected readonly totalPersonales = computed(() => this.personales().length);
  protected readonly pendientes = computed(
    () => this.personales().filter(documento => documento.status === 'PENDING').length
  );
  protected readonly aprobados = computed(
    () => this.personales().filter(documento => documento.status === 'APPROVED').length
  );
  protected readonly rechazados = computed(
    () => this.personales().filter(documento => documento.status === 'REJECTED').length
  );

  ngOnInit(): void {
    this.loadData();
  }

  protected setTab(tab: 'generales' | 'personales'): void {
    this.activeTab.set(tab);
  }

  protected onGeneralFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.generalForm.file = input.files?.[0] ?? null;
  }

  protected onPersonalFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.personalForm.file = input.files?.[0] ?? null;
  }

  protected submitGeneralUpload(): void {
    if (!this.generalForm.file || !this.generalForm.title.trim()) {
      this.submitError.set('Debe completar el título y seleccionar un archivo.');
      return;
    }

    this.submitting.set(true);
    this.submitError.set('');

    this.documentosService.uploadGeneral(
      this.generalForm.file,
      this.generalForm.title.trim(),
      this.generalForm.category,
      this.generalForm.description.trim(),
      this.generalForm.version || '1.0'
    ).subscribe({
      next: () => {
        this.generalForm = {
          title: '',
          category: 'NORMATIVA',
          description: '',
          version: '1.0',
          file: null
        };
        this.submitting.set(false);
        this.loadData();
      },
      error: () => {
        this.submitError.set('No se pudo subir el documento general.');
        this.submitting.set(false);
      }
    });
  }

  protected submitPersonalUpload(): void {
    if (!this.personalForm.file || !this.personalForm.issueDate || !this.personalForm.expirationDate) {
      this.submitError.set('Debe seleccionar un archivo y completar las fechas.');
      return;
    }

    this.submitting.set(true);
    this.submitError.set('');

    const userId = this.personalForm.userId.trim() ? Number(this.personalForm.userId) : undefined;

    this.documentosService.uploadPersonal(
      this.personalForm.file,
      this.personalForm.tipo,
      this.personalForm.issueDate,
      this.personalForm.expirationDate,
      userId
    ).subscribe({
      next: () => {
        this.personalForm = {
          tipo: 'CERTIFICADO_MEDICO',
          issueDate: '',
          expirationDate: '',
          userId: '',
          file: null
        };
        this.submitting.set(false);
        this.loadData();
      },
      error: () => {
        this.submitError.set('No se pudo subir el documento personal.');
        this.submitting.set(false);
      }
    });
  }

  protected getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendiente',
      APPROVED: 'Aprobado',
      REJECTED: 'Rechazado'
    };

    return map[status] ?? status;
  }

  protected getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge badge--warning',
      APPROVED: 'badge badge--success',
      REJECTED: 'badge badge--danger'
    };

    return map[status] ?? 'badge';
  }

  protected formatDate(value?: string): string {
    if (!value) {
      return '—';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return new Intl.DateTimeFormat('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }).format(date);
  }

  private loadData(): void {
    this.loading.set(true);
    this.error.set('');

    forkJoin({
      generales: this.documentosService.getGenerales(),
      personales: this.documentosService.getPersonales()
    }).subscribe({
      next: ({ generales, personales }) => {
        this.generales.set(generales ?? []);
        this.personales.set(personales ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los documentos del sistema.');
        this.loading.set(false);
      }
    });
  }
}
