// src/app/services/page.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Page {
  front: string;
  back: string;
}

@Injectable({ providedIn: 'root' })
export class PageService {
  private pageSubject = new BehaviorSubject<number>(0);
  page$ = this.pageSubject.asObservable();

  setPage(page: number) {
    this.pageSubject.next(page);
  }

  getPage() {
    return this.pageSubject.value;
  }
}