import { Component, Input, forwardRef, signal, computed, ElementRef, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { LucideAngularModule, ChevronDown, Search } from 'lucide-angular';

export interface SearchableSelectOption {
  id: number;
  nombre: string;
}

@Component({
  selector: 'app-searchable-select',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './searchable-select.component.html',
  styleUrl: './searchable-select.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchableSelectComponent),
      multi: true
    }
  ]
})
export class SearchableSelectComponent implements ControlValueAccessor {
  private readonly elementRef = inject(ElementRef);

  @Input() label = '';
  @Input() placeholder = 'Seleccionar...';
  @Input() searchPlaceholder = 'Buscar...';
  @Input() emptyMessage = 'No se encontraron resultados';
  @Input() options: SearchableSelectOption[] = [];
  @Input() showAllOption = false;
  @Input() allOptionLabel = 'Todos';

  protected readonly ChevronDown = ChevronDown;
  protected readonly Search = Search;

  protected open = signal(false);
  protected searchTerm = signal('');
  protected selectedId = signal<number | null>(null);
  protected isDisabled = signal(false);

  protected filteredOptions = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const sorted = [...this.options].sort((a, b) =>
      a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' })
    );

    if (!term) {
      return sorted;
    }

    return sorted.filter(option => option.nombre.toLowerCase().includes(term));
  });

  protected selectedLabel = computed(() => {
    const id = this.selectedId();
    if (id == null) {
      return this.showAllOption ? this.allOptionLabel : '';
    }
    return this.options.find(option => option.id === id)?.nombre ?? '';
  });

  private onChange: (value: number | null) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  writeValue(value: number | null): void {
    this.selectedId.set(value ?? null);
  }

  registerOnChange(fn: (value: number | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled.set(isDisabled);
    if (isDisabled) {
      this.closeDropdown();
    }
  }

  toggleDropdown(): void {
    if (this.isDisabled()) {
      return;
    }

    if (this.open()) {
      this.closeDropdown();
      return;
    }

    this.searchTerm.set('');
    this.open.set(true);
  }

  closeDropdown(): void {
    this.open.set(false);
    this.searchTerm.set('');
    this.onTouched();
  }

  selectOption(option: SearchableSelectOption): void {
    this.selectedId.set(option.id);
    this.onChange(option.id);
    this.closeDropdown();
  }

  selectAllOption(): void {
    this.selectedId.set(null);
    this.onChange(null);
    this.closeDropdown();
  }

  onSearchInput(value: string): void {
    this.searchTerm.set(value);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.closeDropdown();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeDropdown();
  }
}
