export interface ProjetRequestDTO {
  id?: number;
  sujet: string;
}

export interface ProjetResponseDTO {
  id: number;
  sujet: string;
}

export interface ProjetRefDTO {
  id: number;
  sujet?: string;
}

/**
 * Pour compatibilité descendante avec le code existant
 */
export type Projet = ProjetResponseDTO;
