import { Component, input } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button-component.html',
  styleUrl: './button-component.css',
})
export class ButtonComponent {

  public text = input<string>('Button Text')

}
