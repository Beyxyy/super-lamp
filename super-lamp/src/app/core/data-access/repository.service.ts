import { inject, Injectable, signal } from "@angular/core";
import { tap } from "rxjs";

@Injectable({ providedIn: 'root' })
export class Repository<EntityDTO, ApiService> {

  private api! : any;

  data = signal<EntityDTO[]>([]);

  load() {
    this.api.getAll().subscribe((d: EntityDTO[]) => {
      this.data.set(d);
    });
  }

  create(dto: EntityDTO) {
    return this.api.create(dto).pipe(
      tap((newWorkout: EntityDTO) => {
        this.data.update(list => [...list, newWorkout]);
      })
    );
  }

  delete(id: string) {
    return this.api.delete(id).pipe(
      tap(() => {
        this.data.update(list => list.filter((w : any)=> w.id !== id));
      })
    );
  }
}
