import { Component } from '@angular/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';
import { IToast } from '../../../core/interface/toast.interface';

@Component({
  selector: 'app-toast',
  imports: [CommonModule],
  templateUrl: './toastComponent.component.html',
  styleUrl: './toastComponent.component.css'
})
export class ToastComponent {
  toasts: IToast[] = [];
  private sub!: Subscription;

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.sub = this.toastService.visibleToasts.subscribe(t=> {this.toasts = t});
  }

  ngOnDestroy() { this.sub?.unsubscribe(); }
  close(id: number) { this.toastService.remove(id); }
}
