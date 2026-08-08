import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from "@shared/ui/toast/toastComponent.component";
import { LayoutComponent } from './core/layout/layout.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastComponent, LayoutComponent],
  template: `
    <app-layout></app-layout>
    <app-toast/>
  `,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('super-lamp');
}
