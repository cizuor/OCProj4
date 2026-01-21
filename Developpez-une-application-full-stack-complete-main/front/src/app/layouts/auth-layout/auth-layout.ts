import { Component, inject } from '@angular/core';
import { RouterModule } from "@angular/router";
import { Location } from '@angular/common';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterModule],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.css',
})
export class AuthLayout {
  private location = inject(Location);

  goBack() {
    this.location.back();
  }
}
