import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  LucideAngularModule,
  Users,
  Plus,
  Search,
  Eye,
  Edit2,
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
  Loader2,
  CheckCircle,
  AlertCircle
} from 'lucide-angular';

import { TrabajadorService } from '../../services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import {
  Trabajador,
  MaestraItem,
  CapacitacionItem,
  DocumentoItem,
  CrearTrabajadorRequest
} from '../../models/trabajador.model';

@Component({
  selector: 'app-trabajadores-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, LucideAngularModule],
  templateUrl: './trabajadores-list.component.html',
  styleUrl: './trabajadores-list.component.scss'
})
export class TrabajadoresListComponent implements OnInit {
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  // Iconos
  protected readonly Users = Users;
  protected readonly Plus = Plus;
  protected readonly Search = Search;
  protected readonly Eye = Eye;
  protected readonly Edit2 = Edit2;
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
  protected readonly CheckCircle = CheckCircle;
  protected readonly AlertCircle = AlertCircle;

  // Estado del listado y paginación
  protected trabajadores = signal<Trabajador[]>([]);
  protected totalElements = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected currentPage = signal<number>(0);
  protected pageSize = signal<number>(8);
  protected loading = signal<boolean>(true);
  protected searchTerm = signal<string>('');

  // Rol del usuario autenticado
  protected isAdmin = signal<boolean>(false);
  protected userRole = signal<string>('');

  // Modal / Drawer de detalle e inspección
  protected selectedTrabajador = signal<Trabajador | null>(null);
  protected activeDrawerTab = signal<'perfil' | 'capacitaciones' | 'documentos'>('perfil');
  protected capacitaciones = signal<CapacitacionItem[]>([]);
  protected documentos = signal<DocumentoItem[]>([]);
  protected loadingDetail = signal<boolean>(false);

  // Modal de Crear Trabajador (Admin solo)
  protected showCreateModal = signal<boolean>(false);
  protected createForm!: FormGroup;
  protected sedes = signal<MaestraItem[]>([]);
  protected areas = signal<MaestraItem[]>([]);
  protected cargos = signal<MaestraItem[]>([]);
  protected savingTrabajador = signal<boolean>(false);
  protected modalSuccessMsg = signal<string>('');
  protected modalErrorMsg = signal<string>('');

  // Filtro reactivo en cliente por término de búsqueda
  protected filteredTrabajadores = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.trabajadores();
    return this.trabajadores().filter(t =>
      t.nombres.toLowerCase().includes(term) ||
      t.apellidos.toLowerCase().includes(term) ||
      t.numeroDocumento.includes(term) ||
      t.cargoNombre.toLowerCase().includes(term) ||
      t.areaNombre.toLowerCase().includes(term)
    );
  });

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.userRole.set(role);
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    this.initCreateForm();
    this.loadTrabajadores();
    this.loadCatalogos();
  }

  private initCreateForm(): void {
    this.createForm = this.fb.group({
      tipoDocumento: ['DNI', Validators.required],
      numeroDocumento: ['', [Validators.required, Validators.pattern('^[0-9]{8,12}$')]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      telefono: ['', [Validators.pattern('^[0-9]{9}$')]],
      correoNotificaciones: ['', [Validators.email]],
      tipoContrato: ['PERMANENTE', Validators.required],
      sedeId: [1, Validators.required],
      areaId: [1, Validators.required],
      cargoId: [1, Validators.required]
    });
  }

  loadTrabajadores(page: number = 0): void {
    this.loading.set(true);
    this.currentPage.set(page);

    this.trabajadorService.getTrabajadores(page, this.pageSize()).subscribe({
      next: (response) => {
        if (response && response.content) {
          this.trabajadores.set(response.content);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  private loadCatalogos(): void {
    this.trabajadorService.getSedes().subscribe(res => this.sedes.set(res));
    this.trabajadorService.getAreas().subscribe(res => this.areas.set(res));
    this.trabajadorService.getCargos().subscribe(res => this.cargos.set(res));
  }

  // Acciones de Paginación
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

  // Inspeccionar detalle del trabajador
  inspectTrabajador(trabajador: Trabajador, defaultTab: 'perfil' | 'capacitaciones' | 'documentos' = 'perfil'): void {
    this.selectedTrabajador.set(trabajador);
    this.activeDrawerTab.set(defaultTab);
    this.loadingDetail.set(true);

    // Cargar capacitaciones y documentos vinculados al trabajador
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

  // Modal de Crear Trabajador
  openCreateModal(): void {
    if (!this.isAdmin()) return;
    this.modalSuccessMsg.set('');
    this.modalErrorMsg.set('');
    this.createForm.reset({
      tipoDocumento: 'DNI',
      tipoContrato: 'PERMANENTE',
      sedeId: this.sedes()[0]?.id || 1,
      areaId: this.areas()[0]?.id || 1,
      cargoId: this.cargos()[0]?.id || 1
    });
    this.showCreateModal.set(true);
  }

  closeCreateModal(): void {
    this.showCreateModal.set(false);
  }

  submitCreateTrabajador(): void {
    if (this.createForm.invalid || !this.isAdmin()) return;

    this.savingTrabajador.set(true);
    this.modalSuccessMsg.set('');
    this.modalErrorMsg.set('');

    const formVal = this.createForm.value;
    const req: CrearTrabajadorRequest = {
      tipoDocumento: formVal.tipoDocumento,
      numeroDocumento: formVal.numeroDocumento,
      nombres: formVal.nombres,
      apellidos: formVal.apellidos,
      telefono: formVal.telefono || null,
      correoNotificaciones: formVal.correoNotificaciones || null,
      tipoContrato: formVal.tipoContrato,
      sedeId: Number(formVal.sedeId),
      areaId: Number(formVal.areaId),
      cargoId: Number(formVal.cargoId)
    };

    this.trabajadorService.createTrabajador(req).subscribe({
      next: () => {
        this.savingTrabajador.set(false);
        this.modalSuccessMsg.set('Trabajador registrado exitosamente.');
        setTimeout(() => {
          this.closeCreateModal();
          this.loadTrabajadores(0);
        }, 1200);
      },
      error: (err) => {
        this.savingTrabajador.set(false);
        this.modalErrorMsg.set(err.error?.message || 'Error al registrar el trabajador.');
      }
    });
  }
}
