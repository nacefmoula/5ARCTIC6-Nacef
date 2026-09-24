import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjetDetailleRequestDTO, ProjetDetailleResponseDTO } from '../models/projet-detaille.model';
import { Page } from '../models/api-response.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProjetDetailleService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/projet-detaille`;

  getAll(): Observable<ProjetDetailleResponseDTO[]> {
    return this.http.get<ProjetDetailleResponseDTO[]>(`${this.api}/all`);
  }

  getPage(page = 0, size = 10): Observable<Page<ProjetDetailleResponseDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<ProjetDetailleResponseDTO>>(`${this.api}/page`, { params });
  }

  getById(id: number): Observable<ProjetDetailleResponseDTO> {
    return this.http.get<ProjetDetailleResponseDTO>(`${this.api}/get/${id}`);
  }

  getByProjet(projetId: number): Observable<ProjetDetailleResponseDTO[]> {
    return this.http.get<ProjetDetailleResponseDTO[]>(`${this.api}/by-projet/${projetId}`);
  }

  add(p: ProjetDetailleRequestDTO): Observable<ProjetDetailleResponseDTO> {
    return this.http.post<ProjetDetailleResponseDTO>(`${this.api}/add`, p);
  }

  update(p: ProjetDetailleRequestDTO): Observable<ProjetDetailleResponseDTO> {
    return this.http.put<ProjetDetailleResponseDTO>(`${this.api}/update`, p);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/delete/${id}`);
  }

  assignToProjet(pdId: number, projetId: number): Observable<ProjetDetailleResponseDTO> {
    return this.http.put<ProjetDetailleResponseDTO>(`${this.api}/assign-projet/${pdId}/${projetId}`, {});
  }
}
