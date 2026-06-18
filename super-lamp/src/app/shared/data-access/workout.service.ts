import { Injectable } from '@angular/core';
import { BaseEntityService } from './crud.service';
import { Workout } from '../entity/workout.entity';

@Injectable({
  providedIn: 'root'  
})
export class WorkoutService extends BaseEntityService<Workout> {
  protected readonly entityName= "workout";
}