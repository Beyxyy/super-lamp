import Keycloak from 'keycloak-js';
import { Injectable, inject } from '@angular/core';
import { UserService } from '../../shared/data-access/facade/user.service';
import { ToastService } from './toast.service';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private keycloak = new Keycloak({
    url: 'https://auth.anthony-kalbe.fr',
    realm: 'voyage',
    clientId: 'voyage'
  });

  private userService = inject(UserService);
  private s_toast = inject(ToastService);

  async init() {
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',
      checkLoginIframe: false,
      pkceMethod: 'S256',
    });

    if (authenticated) {
      this.s_toast.add("Bienvenue.")
      this.userService.fetchCurrentUser();
      setInterval(() => {
        this.keycloak.updateToken(30);
      }, 10000);
    }

    // return authenticated;
    return Promise.resolve();
  }

  login() {
    return this.keycloak.login();
  }

  logout() {
    this.userService.clearCurrentUser();
    return this.keycloak.logout();
  }

  get token() {
    return this.keycloak.token;
  }

  get isAuthenticated() {
    return !!this.keycloak.token;
  }
}
