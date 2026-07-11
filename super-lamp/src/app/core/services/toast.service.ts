import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { IToast } from "../interface/toast.interface";


@Injectable({ providedIn: 'root' })
export class ToastService {
    private toasts: IToast[] = [];
    private toasts$ = new BehaviorSubject<IToast[]>([]);
    private idCounter = 0;
    private timers = new Map<number, any>();

    // Observable pour les composants
    get visibleToasts() {
        return this.toasts$.asObservable();
    }

    // Ajoute un toast et planifie sa disparition
    add(message: string, duration = 3000, type: IToast['type'] = 'info') {
        const id = ++this.idCounter;
        const toast: IToast = { id, message, type, duration };
        this.toasts.push(toast);
        this.toasts$.next([...this.toasts]);
        const timer = setTimeout(() => {
          console.log("Toast auto-remove", id);
          this.remove(id);
        }, duration);
        this.timers.set(id, timer);
        return id;
    }

    // Supprime un toast (manuel ou automatique)
    remove(id: number) {
        const idx = this.toasts.findIndex(t => t.id === id);
        if (idx === -1) return;
        this.toasts.splice(idx, 1);
        this.toasts$.next([...this.toasts]);
        
        const timer = this.timers.get(id);
        if (timer) {
            clearTimeout(timer);
            this.timers.delete(id);
        }
    }

    // Vide tous les toasts
    clear() {
        this.timers.forEach(t => clearTimeout(t));
        this.timers.clear();
        this.toasts = [];
        this.toasts$.next([]);
    }
}