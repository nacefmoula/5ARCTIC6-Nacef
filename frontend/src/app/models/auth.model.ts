export interface AuthRequestDTO {
  username: string;
  password: string;
}

export interface RegisterRequestDTO {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponseDTO {
  token: string;
  type: string;
  username: string;
  roles: string[];
}

export interface AuthUser {
  username: string;
  roles: string[];
  token: string;
}
