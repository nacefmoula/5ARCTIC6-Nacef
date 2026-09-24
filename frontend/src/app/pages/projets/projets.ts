import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ProjetService } from '../../services/projet.service';
import { ToastService } from '../../services/toast.service';
import { ProjetRequestDTO, ProjetResponseDTO } from '../../models/projet.model';
import { PaginationComponent } from '../../components/pagination/pagination';
import { ProblemDetail } from '../../models/api-response.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-projets',
  imports: [FormsModule, PaginationComponent],
  templateUrl: './projets.html',
  styleUrl: './projets.css'
})
export class ProjetsComponent implements OnInit {
  private service = inject(ProjetService);
  private toast = inject(ToastService);
  private destroyRef = inject(DestroyRef);

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

  current = signal<ProjetRequestDTO>({ sujet: '' });
  fieldErrors = signal<Record<string, string>>({});

  filteredProjets = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) return this.projets();
    return this.projets().filter(p =>
      p.sujet.toLowerCase().includes(term) ||
      p.id.toString().includes(term)
    );
  });

  ngOnInit(): void {
    this.load();
  }

  load(page = this.currentPage(), size = this.pageSize()): void {
    this.loading.set(true);
    this.service.getPage(page, size)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.projets.set(res.content);
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
    this.current.set({ sujet: '' });
    this.fieldErrors.set({});
    this.editMode.set(false);
    this.showForm.set(true);
  }

  edit(p: ProjetResponseDTO): void {
    this.current.set({
      id: p.id,
      sujet: p.sujet
    });
    this.fieldErrors.set({});
    this.editMode.set(true);
    this.showForm.set(true);
  }

  save(): void {
    this.saving.set(true);
    this.fieldErrors.set({});

    const payload = this.current();
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
              ? `Le projet « ${saved.sujet} » a été mis à jour.`
              : `Le projet « ${saved.sujet} » a été créé avec succès.`
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

  delete(id: number, sujet: string): void {
    if (confirm(`Confirmez-vous la suppression définitive du projet « ${sujet} » (#${id}) ?`)) {
      this.service.delete(id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.toast.success('Suppression réussie', `Le projet « ${sujet} » a été supprimé.`);
            this.load();
          }
        });
    }
  }

  cancel(): void {
    this.showForm.set(false);
    this.fieldErrors.set({});
  }

  updateField(field: keyof ProjetRequestDTO, event: Event): void {
    const input = event.target as HTMLInputElement;
    this.current.update(c => ({ ...c, [field]: input.value }));
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
