import { Component } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';
import { Observable } from 'rxjs';
import { IToast } from '@core/interface/toast.interface';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule, AsyncPipe],
  templateUrl: './toastComponent.component.html',
  styleUrl: './toastComponent.component.css'
})
export class ToastComponent {
  public  toasts$! : Observable<IToast[]>

  constructor(private s_toast: ToastService) {
    this.toasts$ = this.s_toast.visibleToasts;
    
  }

  close(id: number) { this.s_toast.remove(id); }
}
