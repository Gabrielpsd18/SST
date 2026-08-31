import { Component, inject, OnInit, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import {
  LucideUsers,
  LucidePlus,
  LucideSearch,
  LucideEye,
  LucideBookOpen,
  LucideFileText,
  LucideChevronLeft,
  LucideChevronRight,
  LucideX,
  LucideShield,
  LucideBuilding2,
  LucideBriefcase,
  LucidePhone,
  LucideMail,
  LucideAward,
  LucideLoader2,
  LucideUpload
} from '@lucide/angular';

import { TrabajadorService } from '../../services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import {
  Trabajador,
  CapacitacionItem,
  DocumentoItem
} from '../../models/trabajador.model';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-trabajadores-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    LucideUsers,
    LucidePlus,
    LucideSearch,
    LucideEye,
    LucideBookOpen,
    LucideFileText,
    LucideChevronLeft,
    LucideChevronRight,
    LucideX,
    LucideShield,
    LucideBuilding2,
    LucideBriefcase,
    LucidePhone,
    LucideMail,
    LucideAward,
    LucideLoader2,
    LucideUpload
  ],
  templateUrl: './trabajadores-list.component.html',
  styleUrl: './trabajadores-list.component.scss'
})
export class TrabajadoresListComponent implements OnInit, OnDestroy {
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly Users = LucideUsers;
  protected readonly Plus = LucidePlus;
  protected readonly Search = LucideSearch;
  protected readonly Eye = LucideEye;
  protected readonly BookOpen = LucideBookOpen;
  protected readonly FileText = LucideFileText;
  protected readonly ChevronLeft = LucideChevronLeft;
  protected readonly ChevronRight = LucideChevronRight;
  protected readonly X = LucideX;
  protected readonly Shield = LucideShield;
  protected readonly Building2 = LucideBuilding2;
  protected readonly Briefcase = LucideBriefcase;
  protected readonly Phone = LucidePhone;
  protected readonly Mail = LucideMail;
  protected readonly Award = LucideAward;
  protected readonly Loader2 = LucideLoader2;
  protected readonly Upload = LucideUpload;

  protected trabajadores = signal<Trabajador[]>([]);
  protected totalElements = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected currentPage = signal<number>(0);
  protected pageSize = signal<number>(8);
  protected loading = signal<boolean>(true);
  protected searchTerm = signal<string>('');
  protected estadoFiltro = signal<'TODOS' | 'ACTIVO' | 'INACTIVO'>('ACTIVO');

  protected isAdmin = signal<boolean>(false);
  protected userRole = signal<string>('');

  protected selectedTrabajador = signal<Trabajador | null>(null);
  protected activeDrawerTab = signal<'perfil' | 'capacitaciones' | 'documentos'>('perfil');
  protected capacitaciones = signal<CapacitacionItem[]>([]);
  protected documentos = signal<DocumentoItem[]>([]);
  protected loadingDetail = signal<boolean>(false);

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  protected isSearching = computed(() => {
    return this.searchTerm().toLowerCase().trim().length > 0;
  });

  protected filteredTrabajadores = computed(() => {
    return this.trabajadores();
  });

  constructor() {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe((term) => {
        if (term.trim().length > 0) {
          this.performSearch(term);
        } else {
          this.loadTrabajadores(0);
        }
      });
  }

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.userRole.set(role);
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');
    this.loadTrabajadores();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearchChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchTerm.set(value);
    this.searchSubject.next(value);
  }

  private performSearch(segment: string): void {
    this.loading.set(true);
    this.currentPage.set(0);
    this.totalPages.set(1);

    const soloActivos = this.estadoFiltro() === 'ACTIVO';

    this.trabajadorService.searchTrabajadores(segment, 8, soloActivos).subscribe({
      next: (results) => {
        this.trabajadores.set(results || []);
        this.totalElements.set(results?.length ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.trabajadores.set([]);
        this.totalElements.set(0);
        this.loading.set(false);
      }
    });
  }

  loadTrabajadores(page: number = 0): void {
    this.loading.set(true);
    this.currentPage.set(page);

    const estado = this.estadoFiltro();

    this.trabajadorService.getTrabajadores(
      page,
      this.pageSize(),
      estado === 'TODOS' ? undefined : estado
    ).subscribe({
      next: (response) => {
        if (response && response.content) {
          this.trabajadores.set(response.content);
          this.totalElements.set(response.totalElements ?? 0);
          this.totalPages.set(response.totalPages ?? 0);
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  setEstadoFiltro(estado: 'TODOS' | 'ACTIVO' | 'INACTIVO'): void {
    this.estadoFiltro.set(estado);
    this.searchTerm.set('');
    this.loadTrabajadores(0);
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.loadTrabajadores(this.currentPage() - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.loadTrabajadores(this.currentPage() + 1);
    }
  }

  inspectTrabajador(
    trabajador: Trabajador,
    defaultTab: 'perfil' | 'capacitaciones' | 'documentos' = 'perfil'
  ): void {
    this.selectedTrabajador.set(trabajador);
    this.activeDrawerTab.set(defaultTab);
    this.loadingDetail.set(true);

    this.trabajadorService.getCapacitacionesByTrabajador(trabajador.id).subscribe({
      next: (caps) => this.capacitaciones.set(caps || []),
      error: () => this.capacitaciones.set([])
    });

    this.trabajadorService.getDocumentosByTrabajador(trabajador.id).subscribe({
      next: (docs) => this.documentos.set(docs || []),
      error: () => this.documentos.set([]),
      complete: () => this.loadingDetail.set(false)
    });
  }

  closeDrawer(): void {
    this.selectedTrabajador.set(null);
  }

  setDrawerTab(tab: 'perfil' | 'capacitaciones' | 'documentos'): void {
    this.activeDrawerTab.set(tab);
  }

  getInitials(nombreCompleto: string): string {
    const parts = nombreCompleto.trim().split(/\s+/);

    if (parts.length === 0 || !parts[0]) {
      return '?';
    }

    if (parts.length === 1) {
      return parts[0][0]?.toUpperCase() ?? '?';
    }

    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  goToNuevoTrabajador(): void {
    this.router.navigate(['/trabajadores/nuevo']);
  }

  goToImportarTrabajadores(): void {
    this.router.navigate(['/trabajadores/importar']);
  }
}