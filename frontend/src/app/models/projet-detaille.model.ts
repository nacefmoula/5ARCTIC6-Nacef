import { ProjetRefDTO, ProjetResponseDTO } from './projet.model';

export interface ProjetDetailleRequestDTO {
  id?: number;
  description: string;
  technologie: string;
  coutProvisoire: number;
  dateDebut: string;
  projetId?: number;
  projet?: ProjetRefDTO;
}

export interface ProjetDetailleResponseDTO {
  id: number;
  description: string;
  technologie: string;
  coutProvisoire: number;
  dateDebut: string;
  projet?: ProjetResponseDTO | null;
}

/**
 * Pour compatibilité descendante avec le code existant
 */
export type ProjetDetaille = ProjetDetailleResponseDTO;
