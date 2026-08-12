import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import {
  LucideAngularModule,
  Users,
  Plus,
  Search,
  Eye,
  BookOpen,
  FileText,
  ChevronLeft,
  ChevronRight,
  X,
  Shield,
  Building2,
  Briefcase,
  Phone,
  Mail,
  Award,
  Loader2
} from 'lucide-angular';

import { TrabajadorService } from '../../services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import {
  Trabajador,
  CapacitacionItem,
  DocumentoItem
} from '../../models/trabajador.model';

@Component({
  selector: 'app-trabajadores-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LucideAngularModule],
  templateUrl: './trabajadores-list.component.html',
  styleUrl: './trabajadores-list.component.scss'
})
export class TrabajadoresListComponent implements OnInit {
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);

  protected readonly Users = Users;
  protected readonly Plus = Plus;
  protected readonly Search = Search;
  protected readonly Eye = Eye;
  protected readonly BookOpen = BookOpen;
  protected readonly FileText = FileText;
  protected readonly ChevronLeft = ChevronLeft;
  protected readonly ChevronRight = ChevronRight;
  protected readonly X = X;
  protected readonly Shield = Shield;
  protected readonly Building2 = Building2;
  protected readonly Briefcase = Briefcase;
  protected readonly Phone = Phone;
  protected readonly Mail = Mail;
  protected readonly Award = Award;
  protected readonly Loader2 = Loader2;
  private readonly router = inject(Router);

  protected trabajadores = signal<Trabajador[]>([]);
  protected totalElements = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected currentPage = signal<number>(0);
  protected pageSize = signal<number>(8);
  protected loading = signal<boolean>(true);
  protected searchTerm = signal<string>('');

  protected isAdmin = signal<boolean>(false);
  protected userRole = signal<string>('');

  protected selectedTrabajador = signal<Trabajador | null>(null);
  protected activeDrawerTab = signal<'perfil' | 'capacitaciones' | 'documentos'>('perfil');
  protected capacitaciones = signal<CapacitacionItem[]>([]);
  protected documentos = signal<DocumentoItem[]>([]);
  protected loadingDetail = signal<boolean>(false);

  protected filteredTrabajadores = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.trabajadores();

    return this.trabajadores().filter(t =>
      t.nombreCompleto.toLowerCase().includes(term) ||
      t.numeroDocumento.includes(term) ||
      t.cargoNombre.toLowerCase().includes
      (term) ||
      t.sedeNombre.toLowerCase().includes(term)
    );
  });

  goToNuevoTrabajador(): void {
    this.router.navigate(['/trabajadores/nuevo']);
  }

  goToImportarTrabajadores(): void {
    this.router.navigate(['/trabajadores/importar']);
  }

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.userRole.set(role);
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    this.loadTrabajadores();
  }


  loadTrabajadores( page: number = 0): void {
    this.loading.set(true);
    this.currentPage.set(0);

    this.trabajadorService.getTrabajadores(page, this.pageSize()).subscribe({
      next: (response) => {
        if (response && response.content) {
          this.trabajadores.set(response.content);
          this.totalElements.set(response.totalElements);
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
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

  inspectTrabajador(trabajador: Trabajador, defaultTab: 'perfil' | 'capacitaciones' | 'documentos' = 'perfil'): void {
    this.selectedTrabajador.set(trabajador);
    this.activeDrawerTab.set(defaultTab);
    this.loadingDetail.set(true);

    this.trabajadorService.getCapacitacionesByTrabajador(trabajador.id).subscribe(caps => {
      this.capacitaciones.set(caps);
    });

    this.trabajadorService.getDocumentosByTrabajador(trabajador.id).subscribe(docs => {
      this.documentos.set(docs);
      this.loadingDetail.set(false);
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
    if (parts.length === 0) return '?';
    if (parts.length === 1) return parts[0][0]?.toUpperCase() ?? '?';
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

}
