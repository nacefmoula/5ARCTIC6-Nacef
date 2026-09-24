import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EntrepriseRequestDTO, EntrepriseResponseDTO } from '../models/entreprise.model';
import { Page } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EntrepriseService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/entreprise`;

  getAll(): Observable<EntrepriseResponseDTO[]> {
    return this.http.get<EntrepriseResponseDTO[]>(`${this.api}/all`);
  }

  getPage(page = 0, size = 10): Observable<Page<EntrepriseResponseDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EntrepriseResponseDTO>>(`${this.api}/page`, { params });
  }

  getById(id: number): Observable<EntrepriseResponseDTO> {
    return this.http.get<EntrepriseResponseDTO>(`${this.api}/get/${id}`);
  }

  add(e: EntrepriseRequestDTO): Observable<EntrepriseResponseDTO> {
    return this.http.post<EntrepriseResponseDTO>(`${this.api}/add`, e);
  }

  update(e: EntrepriseRequestDTO): Observable<EntrepriseResponseDTO> {
    return this.http.put<EntrepriseResponseDTO>(`${this.api}/update`, e);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/delete/${id}`);
  }
}
