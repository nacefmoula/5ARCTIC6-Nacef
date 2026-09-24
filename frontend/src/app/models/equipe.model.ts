import { EntrepriseRefDTO, EntrepriseResponseDTO } from './entreprise.model';

export interface EquipeRequestDTO {
  id?: number;
  nom: string;
  specialite: string;
  entrepriseId?: number;
  entreprise?: EntrepriseRefDTO;
}

export interface EquipeResponseDTO {
  id: number;
  nom: string;
  specialite: string;
  entreprise?: EntrepriseResponseDTO | null;
}

/**
 * Pour compatibilité descendante avec le code existant
 */
export type Equipe = EquipeResponseDTO;
