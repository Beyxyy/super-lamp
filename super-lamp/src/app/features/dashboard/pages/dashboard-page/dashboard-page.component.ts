import { Component } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-page',
  imports: [],
  templateUrl: './dashboard-page.component.html',
})
export class DashboardPageComponent {
  constructor(private authService : AuthService){}

}
