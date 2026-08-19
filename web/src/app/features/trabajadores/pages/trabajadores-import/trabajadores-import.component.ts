import { Component, OnInit, inject, ChangeDetectorRef } from "@angular/core";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImportacionService } from '../../services/importacion.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-trabajadores-import',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trabajadores-import.component.html',
  styleUrl: './trabajadores-import.component.scss'
})
export class TrabajadoresImportComponent implements OnInit {
  private readonly importService = inject(ImportacionService);
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  protected selectedFile: File | null = null;
  protected monthOption: 'THIS' | 'NEXT' | 'PREV' = 'THIS';
  protected importResult: any = null;
  protected pendingErrors: any[] = [];
  protected loading = false;

  ngOnInit(): void {
    this.loadPendingErrors();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  loadPendingErrors() {
    this.importService.getPendingErrors().subscribe({
      next: (errors: any[]) => {
        this.pendingErrors = errors;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error("Error al cargar incidencias pendientes:", err);
      }
    });
  }

  uploadAndProcess() {
    if (!this.selectedFile) {
      this.notificationService.showWarning("Por favor, seleccione un archivo Excel.");
      return;
    }
    this.loading = true;
    this.importResult = null;
    this.cdr.detectChanges();

    this.importService.uploadAndImplement(this.selectedFile, this.monthOption).subscribe({
      next: (res: any) => {
        this.importResult = res;
        this.loading = false;
        this.loadPendingErrors();
        this.cdr.detectChanges();

        if (res.errorsCount > 0 || res.duplicates > 0) {
          this.notificationService.showWarning(
            `Importación procesada. Se guardaron ${res.correctRows} registros. Se detectaron ${res.errorsCount} incidencias y ${res.duplicates} duplicados.`,
            6000
          );
        } else {
          this.notificationService.showSuccess(
            `¡Importación procesada y guardada con éxito! Se guardaron ${res.correctRows} registros.`,
            5000
          );
        }
      },
      error: (err: any) => {
        console.error("Error en la importación:", err);
        this.loading = false;
        this.notificationService.showError(
          "Error al procesar el archivo. Verifique el formato e intente nuevamente.",
          6000
        );
        this.cdr.detectChanges();
      }
    });
  }

  fixAndResendRow(errorRow: any) {
    if (!errorRow.dni || !/^[0-9]{6,20}$/.test(errorRow.dni.trim())) {
      this.notificationService.showError("El DNI es inválido (debe tener entre 6 y 20 dígitos numéricos).");
      return;
    }
    if (!errorRow.trabajador || errorRow.trabajador.trim().length === 0) {
      this.notificationService.showError("El nombre del trabajador no puede estar vacío.");
      return;
    }
    if (!errorRow.sede || errorRow.sede.trim().length === 0) {
      this.notificationService.showError("La sede no puede estar vacía.");
      return;
    }
    if (!errorRow.cargo || errorRow.cargo.trim().length === 0) {
      this.notificationService.showError("El cargo no puede estar vacío.");
      return;
    }

    this.importService.retryRow(errorRow).subscribe({
      next: () => {
        this.notificationService.showSuccess("Trabajador guardado con éxito.");
        this.loadPendingErrors();
        if (this.importResult && this.importResult.errors) {
          this.importResult.errors = this.importResult.errors.filter((r: any) => r !== errorRow);
        }
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error("No se pudo corregir la fila", err);
        this.notificationService.showError(
          `No se pudo guardar el registro: ${err.error?.message || 'Verifique los datos.'}`
        );
        this.cdr.detectChanges();
      }
    });
  }

  discardError(id: number) {
    if (!id) return;
    if (confirm("¿Está seguro de que desea descartar esta incidencia?")) {
      this.importService.deleteError(id).subscribe({
        next: () => {
          this.notificationService.showSuccess("Incidencia descartada correctamente.");
          this.loadPendingErrors();
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          console.error("Error al descartar incidencia:", err);
          this.notificationService.showError("No se pudo descartar la incidencia.");
        }
      });
    }
  }

  protected isFieldInvalid(row: any, fieldName: string): boolean {
    if (!row) return false;
    const error = (row.errorMessage || '').toLowerCase();

    if (fieldName === 'dni') {
      const dniVal = (row.dni || '').trim();
      return error.includes('dni') || !dniVal || !/^[0-9]{6,20}$/.test(dniVal);
    }
    if (fieldName === 'sede') {
      const sedeVal = (row.sede || '').trim();
      return error.includes('sede') || !sedeVal;
    }
    if (fieldName === 'cargo') {
      const cargoVal = (row.cargo || '').trim();
      return error.includes('cargo') || !cargoVal;
    }
    if (fieldName === 'trabajador') {
      const trabVal = (row.trabajador || '').trim();
      return !trabVal;
    }

    return false;
  }
}