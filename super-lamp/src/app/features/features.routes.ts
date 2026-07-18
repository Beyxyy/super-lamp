import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path: "",
        loadComponent: () => import("./home-page/home-page-component/home-page-component")
          .then(c => c.HomePageComponent)
    },
    {
        path: "dairy",
        loadComponent: () => import("./diairy/diairy.component")
          .then(c => c.DiairyComponent)
    },
    {
        path : "fitness",
        loadChildren :  () => import('./fitness/fitness.routes').then(m => m.routes)
    }

];
