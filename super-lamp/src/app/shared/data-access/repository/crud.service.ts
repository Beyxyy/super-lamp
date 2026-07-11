import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'  
})
export abstract class CrudService<T> {
  protected abstract readonly entityName: string;
  protected readonly apiUrl = ``;

  constructor(protected http: HttpClient) {}

  /**
   * Get all entities
   */
  getAll(): Observable<T[]> {
    return this.http.get<T[]>(this.apiUrl);
  }

  /**
   * Get a single entity by ID
   * @param id Entity ID
   */
  getById(id: string): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new entity
   * @param entity Data to create
   */
  create(entity: Omit<T, 'id'>): Observable<T> {
    return this.http.post<T>(this.apiUrl, entity);
  }

  /**
   * Update an existing entity
   * @param id Entity ID
   * @param entity Data to update
   */
  update(id: string, entity: Partial<T>): Observable<T> {
    return this.http.patch<T>(`${this.apiUrl}/${id}`, entity);
  }

  /**
   * Delete an entity
   * @param id Entity ID
   */
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
