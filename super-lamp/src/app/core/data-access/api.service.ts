import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";

@Injectable({
    providedIn:"root"
})
export abstract class ApiService<EntityDTO>{

  private endpoint : string = "";  
    
  private http = inject(HttpClient);

  getAll() {
    return this.http.get<EntityDTO[]>('/api/'+this.endpoint);
  }

  getById(id: string) {
    return this.http.get<EntityDTO>(`/api/${this.endpoint}/${id}`);
  }

  create(dto: EntityDTO) {
    return this.http.post<EntityDTO>('/api/workouts', dto);
  }

  delete(id: string) {
    return this.http.delete(`/api/workouts/${id}`);
  }
}