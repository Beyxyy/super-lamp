import Keycloak from 'keycloak-js';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private keycloak = new Keycloak({
    url: 'http://127.0.0.1:8080',
    realm: 'superLamp',
    clientId: 'superLamp'
  });

  async init() {
    // return  this.keycloak.init({
    //   onLoad: 'login-required',
    //   checkLoginIframe: false
    // }).then(() => {
    // setInterval(() => {
    //   this.keycloak.updateToken(30);
    // }, 10000);
    // });
    return Promise.resolve();
  }

  login() {
    return this.keycloak.login();
  }

  logout() {
    return this.keycloak.logout();
  }

  get token() {
    return this.keycloak.token;
  }

  get isAuthenticated() {
    return !!this.keycloak.token;
  }

  get username() {
    return this.keycloak.tokenParsed?.["preferred_username"];
  }
}
