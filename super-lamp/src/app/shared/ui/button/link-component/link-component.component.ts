import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, QueryParamsHandling } from '@angular/router';

@Component({
  selector: 'app-link',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './link-component.component.html',
  styleUrls: ['./link-component.component.scss']
})
export class LinkComponent {
  @Input() label: string = '';
  @Input() routerLink: string | any[] = '';
  @Input() target: string = '_self';
  @Input() rel: string = '';
  @Input() class: string = '';
  @Input() disabled: boolean = false;
  @Input() queryParamsHandling: QueryParamsHandling = 'merge';
}
