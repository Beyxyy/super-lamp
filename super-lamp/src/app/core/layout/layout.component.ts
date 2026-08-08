import { Component } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent],
  template: `
    <app-header></app-header>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [ `
    .main-content {
      padding: 20px;
      min-height: calc(100vh - 60px);
      margin: 0 auto;
      max-width: 1200px;
    }
  `]
})
export class LayoutComponent {}
