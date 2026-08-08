import { Component, Renderer2, OnInit, OnDestroy } from '@angular/core';
import { MenuService } from './menu.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './menu-component.html',
  styleUrls: ['./menu-component.css'],
})
export class MenuComponent implements OnInit, OnDestroy {
  constructor(
    public menuService: MenuService,
    private renderer: Renderer2
  ) {}

  ngOnInit() {
    this.menuService.isOpen$.subscribe(isOpen => {
      if (isOpen) {
        this.renderer.addClass(document.body, 'menu-open');
      } else {
        this.renderer.removeClass(document.body, 'menu-open');
      }
    });
  }

  ngOnDestroy() {
    this.renderer.removeClass(document.body, 'menu-open');
  }

  toggleMenu() {
    this.menuService.toggle();
  }
}
