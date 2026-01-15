import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { SessionService } from 'src/app/core/services/session.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private sessionService = inject(SessionService);
  private router = inject(Router);

  public error = false;

  // Définition du formulaire
  public form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    pseudo: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  public submit(): void {    
    this.authService.register(this.form.value as any).subscribe({
      next: () => this.router.navigate(['/login']),
      error: _ => this.error = true
    });
  }
}
