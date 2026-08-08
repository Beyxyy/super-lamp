import { Component } from "@angular/core";
import { FullPageLayout } from "@shared/ui/layout/full-page.layout";


@Component({
  standalone : true,
  selector: 'app-home-page',
  templateUrl: `home-page.component.html`,
  styleUrl : 'home-page.component.css',
  imports : [FullPageLayout]
})
export class HomePageComponent{
}