import { Component } from "@angular/core";
import { FullPageLayout } from "@shared/ui/layout/full-page.layout";


@Component({
  standalone : true,
  selector: 'app-Fitness-page',
  templateUrl: `fitness.component.html`,
  styleUrl : 'fitness.component.css',
  imports : [FullPageLayout]
})
export class FitnessComponent{
}