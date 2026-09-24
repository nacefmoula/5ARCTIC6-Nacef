import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, signal } from '@angular/core';
import { PaginationComponent } from './pagination';

@Component({
  template: `
    <app-pagination
      [page]="page()"
      [totalPages]="totalPages()"
      [totalElements]="totalElements()"
      [pageSize]="pageSize()"
      (pageChange)="onPageChange($event)"
      (pageSizeChange)="onPageSizeChange($event)"
    />
  `,
  imports: [PaginationComponent]
})
class TestHostComponent {
  page = signal(0);
  totalPages = signal(5);
  totalElements = signal(45);
  pageSize = signal(10);

  lastPageChange: number | null = null;
  lastPageSizeChange: number | null = null;

  onPageChange(p: number) {
    this.lastPageChange = p;
    this.page.set(p);
  }

  onPageSizeChange(s: number) {
    this.lastPageSizeChange = s;
    this.pageSize.set(s);
  }
}

describe('PaginationComponent', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let host: TestHostComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    host = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should render pagination info correctly', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Affichage de 1 à 10 sur 45 éléments');
  });

  it('should emit pageChange when clicking next button', () => {
    const nextBtn = fixture.nativeElement.querySelector('.btn-nav[title="Page suivante"]') as HTMLButtonElement;
    expect(nextBtn).toBeTruthy();
    nextBtn.click();
    fixture.detectChanges();
    expect(host.lastPageChange).toBe(1);
  });

  it('should disable previous button on first page', () => {
    const prevBtn = fixture.nativeElement.querySelector('.btn-nav[title="Page précédente"]') as HTMLButtonElement;
    expect(prevBtn.disabled).toBe(true);
  });
});
