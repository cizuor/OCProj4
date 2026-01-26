import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { SessionService } from 'src/app/core/services/session.service';

@Component({
  selector: 'app-accueil-component',
  imports: [RouterLink],
  templateUrl: './accueil-component.html',
  styleUrl: './accueil-component.css',
})
export class AccueilComponent implements OnInit{
  private sessionService = inject(SessionService);
  private router = inject(Router);

  ngOnInit() {
    // Si on arrive sur l'accueil mais qu'on est déjà logué
    this.sessionService.$isLogged().subscribe((logged) => {
      if (logged) {
        this.router.navigate(['/articles']);
      }
    });
  }
}
