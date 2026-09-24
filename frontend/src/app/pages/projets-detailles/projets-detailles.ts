import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjetDetailleService } from '../../services/projet-detaille.service';
import { ProjetService } from '../../services/projet.service';
import { ToastService } from '../../services/toast.service';
import { ProjetDetailleRequestDTO, ProjetDetailleResponseDTO } from '../../models/projet-detaille.model';
import { ProjetResponseDTO } from '../../models/projet.model';
import { PaginationComponent } from '../../components/pagination/pagination';
import { ProblemDetail } from '../../models/api-response.model';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-projets-detailles',
  imports: [FormsModule, DecimalPipe, PaginationComponent],
  templateUrl: './projets-detailles.html',
  styleUrl: './projets-detailles.css'
})
export class ProjetsDetaillesComponent implements OnInit {
  private service = inject(ProjetDetailleService);
  private projetService = inject(ProjetService);
  private toast = inject(ToastService);
  private destroyRef = inject(DestroyRef);
  public authService = inject(AuthService);

  projetsDetailles = signal<ProjetDetailleResponseDTO[]>([]);
  projets = signal<ProjetResponseDTO[]>([]);

  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = signal(10);
  searchTerm = signal('');

  loading = signal(false);
  saving = signal(false);
  showForm = signal(false);
  editMode = signal(false);

  current = signal<ProjetDetailleRequestDTO>({
    description: '',
    technologie: '',
    coutProvisoire: 0,
    dateDebut: ''
  });
  selectedProjetId = signal<number | null>(null);
  fieldErrors = signal<Record<string, string>>({});

  filteredProjetsDetailles = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) return this.projetsDetailles();
    return this.projetsDetailles().filter(pd =>
      pd.description.toLowerCase().includes(term) ||
      pd.technologie.toLowerCase().includes(term) ||
      (pd.projet?.sujet?.toLowerCase().includes(term) ?? false) ||
      pd.id.toString().includes(term)
    );
  });

  ngOnInit(): void {
    this.load();
    this.loadProjets();
  }

  load(page = this.currentPage(), size = this.pageSize()): void {
    this.loading.set(true);
    this.service.getPage(page, size)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.projetsDetailles.set(res.content);
          this.totalElements.set(res.totalElements);
          this.totalPages.set(res.totalPages);
          this.currentPage.set(res.number);
          this.loading.set(false);
        },
        error: () => {
          this.loading.set(false);
        }
      });
  }

  loadProjets(): void {
    this.projetService.getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => this.projets.set(data)
      });
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.load(page, this.pageSize());
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0);
    this.load(0, size);
  }

  openAdd(): void {
    const today = new Date().toISOString().split('T')[0];
    this.current.set({
      description: '',
      technologie: '',
      coutProvisoire: 0,
      dateDebut: today
    });
    this.selectedProjetId.set(null);
    this.fieldErrors.set({});
    this.editMode.set(false);
    this.showForm.set(true);
  }

  edit(pd: ProjetDetailleResponseDTO): void {
    this.current.set({
      id: pd.id,
      description: pd.description,
      technologie: pd.technologie,
      coutProvisoire: pd.coutProvisoire,
      dateDebut: pd.dateDebut
    });
    this.selectedProjetId.set(pd.projet?.id ?? null);
    this.fieldErrors.set({});
    this.editMode.set(true);
    this.showForm.set(true);
  }

  save(): void {
    this.saving.set(true);
    this.fieldErrors.set({});

    const pId = this.selectedProjetId();
    const payload: ProjetDetailleRequestDTO = {
      ...this.current(),
      projetId: pId ?? undefined,
      projet: pId ? { id: pId } : undefined
    };

    const obs = this.editMode()
      ? this.service.update(payload)
      : this.service.add(payload);

    obs.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (saved) => {
          this.saving.set(false);
          this.toast.success(
            'Succès',
            this.editMode()
              ? `Le projet détaillé #${saved.id} a été mis à jour.`
              : `Le projet détaillé #${saved.id} a été créé avec succès.`
          );
          this.cancel();
          this.load();
        },
        error: (err: HttpErrorResponse) => {
          this.saving.set(false);
          if (err.status === 400 && err.error?.invalidParams) {
            const problem = err.error as ProblemDetail;
            if (problem.invalidParams) {
              this.fieldErrors.set(problem.invalidParams);
            }
          }
        }
      });
  }

  delete(id: number): void {
    if (confirm(`Confirmez-vous la suppression du projet détaillé #${id} ?`)) {
      this.service.delete(id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.toast.success('Suppression réussie', `Le projet détaillé #${id} a été supprimé.`);
            this.load();
          }
        });
    }
  }

  cancel(): void {
    this.showForm.set(false);
    this.fieldErrors.set({});
  }

  updateStringField(field: 'description' | 'technologie' | 'dateDebut', event: Event): void {
    const input = event.target as HTMLInputElement;
    this.current.update(c => ({ ...c, [field]: input.value }));
    this.clearFieldError(field);
  }

  updateNumberField(field: 'coutProvisoire', event: Event): void {
    const input = event.target as HTMLInputElement;
    const num = parseFloat(input.value) || 0;
    this.current.update(c => ({ ...c, [field]: num }));
    this.clearFieldError(field);
  }

  onProjetSelect(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const val = select.value ? Number(select.value) : null;
    this.selectedProjetId.set(val);
  }

  private clearFieldError(field: string): void {
    if (this.fieldErrors()[field]) {
      this.fieldErrors.update(errs => {
        const next = { ...errs };
        delete next[field];
        return next;
      });
    }
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }
}
