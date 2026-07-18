import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageComponent } from '../page/page.component';
import { Page } from '@shared/entity/page.entity';
import { PageService } from '@shared/data-access/facade/page.service';
import { PageDirection } from '@pages/home-page/enum/page-direction.enum';

@Component({
  standalone : true,
  selector: 'app-livre',
  imports: [CommonModule, PageComponent],
  templateUrl: './livre.component.html',
  styleUrl: './livre.component.css'
})
export class LivreComponent implements OnInit {
  estOuvert: boolean = false;
  pageCourante: number = 0;
  pages  : Page[]
  constructor(private s_page : PageService) {
    this.pages = s_page.getPages();
  }

  ngOnInit(): void {
    
  }

  public onTurnPage(direction : PageDirection){
    direction == PageDirection.RIGHT ? this.s_page.nextPage() : this.s_page.previousPage();
  }

}
