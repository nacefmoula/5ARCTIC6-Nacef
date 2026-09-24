export interface EntrepriseRequestDTO {
  id?: number;
  nom: string;
  adresse: string;
}

export interface EntrepriseResponseDTO {
  id: number;
  nom: string;
  adresse: string;
}

export interface EntrepriseRefDTO {
  id: number;
  nom?: string;
}

/**
 * Pour compatibilité descendante avec le code existant
 */
export type Entreprise = EntrepriseResponseDTO;
