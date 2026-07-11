import { Component, OnInit} from "@angular/core";
import { PageService } from "@shared/data-access/facade/page.service";
import { LivreComponent } from "../component/livre/livre.component";


@Component({
  standalone : true,
  selector: 'app-home-page',
  templateUrl: 'home-page-component.html',
  styleUrl : 'home-page-component.css',
  imports : [LivreComponent]
})
export class HomePageComponent implements OnInit {
  constructor(private pageService: PageService) {}

  ngOnInit() {
   
  }
}