import { Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { User } from "../../entity/user.entity";
import { Role } from "@shared/enum/role.enum";

@Injectable({providedIn: "root"})
export class UserService {

    private _currentUser = signal<User | null>(null);
    
    constructor(private http: HttpClient) {}

    get currentUser() {
        return this._currentUser();
    }

    fetchCurrentUser(): void {
        // this.http.get<User>('/user').subscribe(user => {
        //     this._currentUser.set(user);
        // });
    }

    clearCurrentUser(): void {
        this._currentUser.set(null);
    }

    get isAdmin() : boolean{
        return this._currentUser()?.role === Role.ADMIN
    }

}