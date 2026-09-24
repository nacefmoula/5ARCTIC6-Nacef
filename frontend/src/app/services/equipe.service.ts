import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EquipeRequestDTO, EquipeResponseDTO } from '../models/equipe.model';
import { Page } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EquipeService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/equipe`;

  getAll(): Observable<EquipeResponseDTO[]> {
    return this.http.get<EquipeResponseDTO[]>(`${this.api}/all`);
  }

  getPage(page = 0, size = 10): Observable<Page<EquipeResponseDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EquipeResponseDTO>>(`${this.api}/page`, { params });
  }

  getById(id: number): Observable<EquipeResponseDTO> {
    return this.http.get<EquipeResponseDTO>(`${this.api}/get/${id}`);
  }

  getByEntreprise(entrepriseId: number): Observable<EquipeResponseDTO[]> {
    return this.http.get<EquipeResponseDTO[]>(`${this.api}/by-entreprise/${entrepriseId}`);
  }

  add(e: EquipeRequestDTO): Observable<EquipeResponseDTO> {
    return this.http.post<EquipeResponseDTO>(`${this.api}/add`, e);
  }

  update(e: EquipeRequestDTO): Observable<EquipeResponseDTO> {
    return this.http.put<EquipeResponseDTO>(`${this.api}/update`, e);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/delete/${id}`);
  }

  assignToEntreprise(equipeId: number, entrepriseId: number): Observable<EquipeResponseDTO> {
    return this.http.put<EquipeResponseDTO>(`${this.api}/assign-entreprise/${equipeId}/${entrepriseId}`, {});
  }

  assignToProjet(equipeId: number, projetId: number): Observable<EquipeResponseDTO> {
    return this.http.put<EquipeResponseDTO>(`${this.api}/assign-projet/${equipeId}/${projetId}`, {});
  }
}
