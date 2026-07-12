import { Component } from "@angular/core";
import { LivreComponent } from "./component/livre/livre.component";
import { FullPageLayout } from "@shared/ui/layout/full-page.layout";


@Component({
  standalone : true,
  selector: 'app-diairy-page',
  templateUrl: `./diairy.component.html`,
  styleUrl : './diairy.component.css',
  imports : [LivreComponent, FullPageLayout]
})
export class DiairyComponent{
}