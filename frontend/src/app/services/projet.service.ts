import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjetRequestDTO, ProjetResponseDTO } from '../models/projet.model';
import { Page } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProjetService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/projet`;

  getAll(): Observable<ProjetResponseDTO[]> {
    return this.http.get<ProjetResponseDTO[]>(`${this.api}/all`);
  }

  getPage(page = 0, size = 10): Observable<Page<ProjetResponseDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<ProjetResponseDTO>>(`${this.api}/page`, { params });
  }

  getById(id: number): Observable<ProjetResponseDTO> {
    return this.http.get<ProjetResponseDTO>(`${this.api}/get/${id}`);
  }

  add(p: ProjetRequestDTO): Observable<ProjetResponseDTO> {
    return this.http.post<ProjetResponseDTO>(`${this.api}/add`, p);
  }

  update(p: ProjetRequestDTO): Observable<ProjetResponseDTO> {
    return this.http.put<ProjetResponseDTO>(`${this.api}/update`, p);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/delete/${id}`);
  }
}
