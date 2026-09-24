import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ProblemDetail } from '../../models/api-response.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toast = inject(ToastService);

  isRegisterMode = signal(false);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  fieldErrors = signal<Record<string, string>>({});

  loginForm: FormGroup = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  registerForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  toggleMode(): void {
    this.isRegisterMode.update(v => !v);
    this.errorMessage.set(null);
    this.fieldErrors.set({});
  }

  fillDemo(userType: 'admin' | 'user'): void {
    if (userType === 'admin') {
      this.loginForm.patchValue({ username: 'admin', password: 'Admin123!' });
    } else {
      this.loginForm.patchValue({ username: 'user', password: 'User123!' });
    }
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.fieldErrors.set({});

    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.toast.success('Connexion réussie', `Bienvenue, ${res.username} !`);
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/entreprises';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.handleError(err);
      }
    });
  }

  onRegister(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.fieldErrors.set({});

    this.authService.register(this.registerForm.value).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.toast.success('Compte créé', `Bienvenue, ${res.username} ! Votre compte a été initialisé.`);
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/entreprises';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.handleError(err);
      }
    });
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.error && typeof err.error === 'object') {
      const problem = err.error as ProblemDetail;
      if (problem.invalidParams) {
        this.fieldErrors.set(problem.invalidParams);
      }
      this.errorMessage.set(problem.detail || problem.title || 'Une erreur est survenue.');
    } else if (err.status === 401) {
      this.errorMessage.set('Nom d\'utilisateur ou mot de passe incorrect.');
    } else {
      this.errorMessage.set('Impossible de se connecter au serveur d\'authentification.');
    }
  }
}
