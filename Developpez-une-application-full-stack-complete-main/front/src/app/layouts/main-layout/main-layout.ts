import { Component, inject } from '@angular/core';
import { Router, RouterModule } from "@angular/router";
import { Location } from '@angular/common';
import { SessionService } from 'src/app/core/services/session.service';

@Component({
  selector: 'app-main-layout',
  imports: [RouterModule],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css',
})
export class MainLayout {
  private sessionService = inject(SessionService);
  private location = inject(Location);
  private router = inject(Router);

  public isMenuOpen = false;

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  goBack() {
    this.location.back();
  }

  logout() {
    this.sessionService.logOut();
    this.router.navigate(['/']); 
  }

}
