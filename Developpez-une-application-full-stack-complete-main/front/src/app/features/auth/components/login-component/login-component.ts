import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { SessionService } from 'src/app/core/services/session.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private sessionService = inject(SessionService);
  private router = inject(Router);

  public error = false;

  // Définition du formulaire
  public form = this.fb.group({
    login: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(3)]]
  });

  public submit(): void {
    const loginRequest = this.form.value as any; // On récupère les valeurs
    
    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        // On stocke le token
        localStorage.setItem('token', response.token);
        
        // On récupère les infos de l'utilisateur pour la session
        this.authService.me().subscribe((user) => {
          this.sessionService.logIn(user);
          this.router.navigate(['/articles']); // Redirection
        });
      },
      error: _ => this.error = true
    });
  }
}
