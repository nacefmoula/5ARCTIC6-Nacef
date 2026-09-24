import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';
import { ProblemDetail } from '../models/api-response.model';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const authService = inject(AuthService, { optional: true });
  const router = inject(Router, { optional: true });

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let title = 'Erreur';
      let message = 'Une erreur inattendue est survenue.';

      if (error.status === 0) {
        title = 'Erreur Réseau';
        message = 'Impossible de contacter le serveur backend. Veuillez vérifier votre connexion ou l\'état du service.';
      } else if (error.status === 401) {
        title = 'Non Authentifié';
        message = 'Votre session a expiré ou est invalide. Veuillez vous reconnecter.';
        authService?.logout();
        if (router && !router.url.includes('/login')) {
          router.navigate(['/login']);
        }
      } else if (error.status === 403) {
        title = 'Accès Refusé';
        message = 'Vous ne possédez pas les autorisations nécessaires (réservé aux administrateurs).';
      } else if (error.error && typeof error.error === 'object') {
        const problem = error.error as ProblemDetail;
        title = problem.title || `Erreur ${error.status}`;

        if (problem.invalidParams && Object.keys(problem.invalidParams).length > 0) {
          const fieldList = Object.entries(problem.invalidParams)
            .map(([field, msg]) => `• ${field}: ${msg}`)
            .join('\n');
          message = `${problem.detail || 'Validation échouée'}\n${fieldList}`;
        } else {
          message = problem.detail || error.message || 'Erreur lors du traitement de la requête.';
        }
      } else if (error.status === 404) {
        title = 'Ressource Introuvable';
        message = 'La ressource demandée n\'existe pas (404).';
      } else if (error.status === 500) {
        title = 'Erreur Serveur';
        message = 'Le serveur a rencontré une erreur interne (500).';
      } else {
        title = `Erreur ${error.status}`;
        message = error.statusText || error.message || 'Erreur HTTP.';
      }

      toastService.error(title, message);
      return throwError(() => error);
    })
  );
};
