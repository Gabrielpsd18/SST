import { Component, Input, Output, EventEmitter, forwardRef, signal, computed, ElementRef, inject, HostListener, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import {  LucideChevronDown, LucideSearch, LucideLoader2 } from '@lucide/angular';
import { Observable, Subject, catchError, debounceTime, distinctUntilChanged, finalize, of, switchMap } from 'rxjs';

export interface SearchableSelectOption {
  id: number;
  nombre: string;
}

@Component({
  selector: 'app-searchable-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
  @Input() multiple = false;
  @Input() hideSelectedDisplay = false;
  @Input() searchFn?: (segment: string, limit: number) => Observable<SearchableSelectOption[]>;
  @Input() maxResults = 8;
  @Output() selectionChange = new EventEmitter<SearchableSelectOption[]>();

  @ViewChild('searchInput') private searchInput?: ElementRef<HTMLInputElement>;

  protected readonly ChevronDown = LucideChevronDown;
  protected readonly Search = LucideSearch;
  protected readonly Loader2 = LucideLoader2;

  protected open = signal(false);
  protected searchTerm = signal('');
  protected selectedIds = signal<number[]>([]);
  protected readonly selectedRows = signal<SearchableSelectOption[]>([]);
  protected isDisabled = signal(false);
  protected loading = signal(false);
  protected remoteOptions = signal<SearchableSelectOption[]>([]);
  protected readonly fallbackSelectedOptions = signal<SearchableSelectOption[]>([]);

  private readonly searchSubject = new Subject<string>();

  protected readonly allOptions = computed(() => {
    const merged = [...this.options, ...this.remoteOptions()];
    const map = new Map<number, SearchableSelectOption>();
    merged.forEach((option) => map.set(option.id, option));
    return [...map.values()].sort((a, b) => a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' }));
  });

  protected readonly selectedId = computed(() => {
    if (this.multiple) {
      return this.selectedIds()[0] ?? null;
    }
    return this.selectedIds()[0] ?? null;
  });

  protected readonly filteredOptions = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const sorted = [...this.allOptions()];

    const filtered = term
      ? sorted.filter((option) => option.nombre.toLowerCase().includes(term))
      : sorted;

    return filtered.slice(0, this.maxResults);
  });

  protected readonly selectedOptions = computed(() => this.selectedRows());

  protected readonly selectedLabel = computed(() => {
    const selected = this.selectedIds();
    if (selected.length === 0) {
      return this.showAllOption ? this.allOptionLabel : '';
    }

    if (this.multiple) {
      const names = this.selectedRows().length > 0
        ? this.selectedRows().map((option) => option.nombre)
        : selected
            .map((id) => this.allOptions().find((option) => option.id === id)?.nombre ?? this.fallbackSelectedOptions().find((option) => option.id === id)?.nombre)
            .filter((name): name is string => Boolean(name));

      if (names.length === 0) {
        return `${selected.length} seleccionados`;
      }

      return names.length <= 2 ? names.join(', ') : `${names.length} seleccionados`;
    }

    const selectedOption = this.selectedRows()[0] ?? this.allOptions().find((option) => option.id === selected[0]) ?? this.fallbackSelectedOptions().find((option) => option.id === selected[0]);
    return selectedOption?.nombre ?? '';
  });

  private onChange: (value: number | number[] | null) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  constructor() {
    this.searchSubject
      .pipe(
        debounceTime(250),
        distinctUntilChanged(),
        switchMap((term) => {
          if (!this.searchFn) {
            return of([] as SearchableSelectOption[]);
          }

          const segment = term.trim();
          this.loading.set(true);
          return this.searchFn(segment, this.maxResults).pipe(
            catchError(() => of([] as SearchableSelectOption[])),
            finalize(() => this.loading.set(false))
          );
        })
      )
      .subscribe((items) => {
        this.remoteOptions.set(items);
      });
  }

  writeValue(value: number | number[] | null): void {
    const normalized = Array.isArray(value)
      ? value.filter((id): id is number => id != null)
      : value == null
        ? []
        : [value];

    this.selectedIds.set(normalized);

    if (this.fallbackSelectedOptions().length > 0) {
      const fallbackRows = normalized
        .map((id) => this.fallbackSelectedOptions().find((option) => option.id === id))
        .filter((option): option is SearchableSelectOption => Boolean(option));
      this.selectedRows.set(fallbackRows);
      this.selectionChange.emit([...fallbackRows]);
      return;
    }

    this.refreshSelectedList();
  }

  @Input()
  set initialSelectedOptions(value: SearchableSelectOption[]) {
    const normalized = value ?? [];
    this.fallbackSelectedOptions.set(normalized);

    if (normalized.length === 0) {
      this.selectedRows.set([]);
      this.selectedIds.set([]);
      this.onChange(this.multiple ? [] : null);
      return;
    }

    const ids = [...new Set(normalized.map((option) => option.id))];
    this.selectedIds.set(ids);
    this.selectedRows.set([...normalized]);
    this.selectionChange.emit([...normalized]);
    this.onChange(this.multiple ? ids : ids[0] ?? null);
  }

  registerOnChange(fn: (value: number | number[] | null) => void): void {
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

    setTimeout(() => {
      this.searchInput?.nativeElement.focus();
    }, 0);

    if (this.searchFn) {
      this.searchSubject.next('');
    }
  }

  closeDropdown(): void {
    this.open.set(false);
    this.searchTerm.set('');
    this.onTouched();
  }

  private refreshSelectedList(): void {
    const rows = this.selectedIds()
      .map((id) => this.allOptions().find((option) => option.id === id) ?? this.fallbackSelectedOptions().find((option) => option.id === id))
      .filter((option): option is SearchableSelectOption => Boolean(option));

    this.selectedRows.set(rows);
    this.selectionChange.emit([...rows]);
  }

  selectOption(option: SearchableSelectOption): void {
    if (this.multiple) {
      const next = new Set(this.selectedIds());
      if (next.has(option.id)) {
        next.delete(option.id);
      } else {
        next.add(option.id);
      }

      const updated = [...next];
      this.selectedIds.set(updated);
      this.refreshSelectedList();
      this.onChange(updated);
      return;
    }

    this.selectedIds.set([option.id]);
    this.refreshSelectedList();
    this.onChange(option.id);
    this.closeDropdown();
  }

  selectAllOption(): void {
    if (this.multiple) {
      this.selectedIds.set([]);
      this.refreshSelectedList();
      this.onChange([]);
      return;
    }

    this.selectedIds.set([]);
    this.refreshSelectedList();
    this.onChange(null);
    this.closeDropdown();
  }

  removeOption(optionId: number): void {
    const next = this.selectedIds().filter((id) => id !== optionId);
    this.selectedIds.set(next);
    this.refreshSelectedList();
    this.onChange(this.multiple ? next : next[0] ?? null);
  }

  onSearchInput(value: string): void {
    this.searchTerm.set(value);
    if (this.searchFn) {
      this.searchSubject.next(value);
    }
  }

  isSelected(optionId: number): boolean {
    return this.selectedIds().includes(optionId);
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
