import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path : "",
        loadComponent : () => import("./fitness.component")
         .then(c => c.FitnessComponent)
    }
]