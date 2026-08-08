import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path : "",
        loadComponent : () => import("./home-page/home-page.component")
         .then(c => c.HomePageComponent)
    }
];
