import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        canMatch: [authGuard],
        loadChildren: () => import('./pages/pages.routes').then(m => m.routes)
    }
];
