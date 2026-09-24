import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  imports: [],
  templateUrl: './pagination.html',
  styleUrl: './pagination.css'
})
export class PaginationComponent {
  page = input.required<number>(); // 0-indexed
  totalPages = input.required<number>();
  totalElements = input.required<number>();
  pageSize = input<number>(10);
  pageSizeOptions = input<number[]>([5, 10, 20]);

  pageChange = output<number>();
  pageSizeChange = output<number>();

  firstItemIndex = computed(() => {
    if (this.totalElements() === 0) return 0;
    return this.page() * this.pageSize() + 1;
  });

  lastItemIndex = computed(() => {
    return Math.min((this.page() + 1) * this.pageSize(), this.totalElements());
  });

  pagesList = computed(() => {
    const total = this.totalPages();
    const cur = this.page();
    const pages: number[] = [];

    if (total <= 7) {
      for (let i = 0; i < total; i++) pages.push(i);
    } else {
      pages.push(0);
      let start = Math.max(1, cur - 1);
      let end = Math.min(total - 2, cur + 1);

      if (cur <= 2) {
        end = 3;
      } else if (cur >= total - 3) {
        start = total - 4;
      }

      if (start > 1) pages.push(-1); // -1 represents ellipsis

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (end < total - 2) pages.push(-2); // -2 represents ellipsis
      pages.push(total - 1);
    }

    return pages;
  });

  goToPage(p: number): void {
    if (p >= 0 && p < this.totalPages() && p !== this.page()) {
      this.pageChange.emit(p);
    }
  }

  onSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const newSize = Number(target.value);
    if (!isNaN(newSize)) {
      this.pageSizeChange.emit(newSize);
    }
  }
}
