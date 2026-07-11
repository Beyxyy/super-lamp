import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideMarkdown } from 'ngx-markdown';
import { routes } from './app.routes';
import { AuthService } from './core/services/auth.service';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideAppInitializer(() => {
      const auth = inject(AuthService);
      return auth.init();
    }),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideMarkdown(),
  ]
};
