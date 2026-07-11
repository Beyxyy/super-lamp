import { Injectable } from '@angular/core';
import { Page } from '@shared/entity/page.entity';
import { BehaviorSubject } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class PageService {
  
  private readonly pages: Page[] = [
    {
      id: 1,
      titre: 'Page 1',
      contenu: '# Contenu de la première page. Bienvenue !'
    },
    {
      id: 2,
      titre: 'Page 2',
      contenu: '# Contenu de la deuxième page. Utilisez les flèches pour naviguer.'
    },
    {
      id: 3,
      titre: 'Page 3',
      contenu: '# Contenu de la troisième page. Vous êtes à la moitié.'
    },
    {
      id: 4,
      titre: 'Page 4',
      contenu: '#Contenu de la quatrième page. Encore une étape !'
    },
    {
      id: 5,
      titre: 'Page 5',
      contenu: '# Contenu de la cinquième page. Dernière page !'
    }
  ];

  // Page courante (index, 0-based)
  private currentPageIndex = new BehaviorSubject<number>(0);
  currentPageIndex$ = this.currentPageIndex.asObservable();

  constructor() {}

  /**
   * Récupère toutes les pages
   */
  getPages(): Page[] {
    return [...this.pages];
  }

  /**
   * Récupère la page courante
   */
  getCurrentPage(): Page {
    return this.pages[this.currentPageIndex.value];
  }

  /**
   * Récupère l'index de la page courante
   */
  getCurrentIndex(): number {
    return this.currentPageIndex.value;
  }

  /**
   * Passe à la page suivante
   */
  nextPage(): void {
    const currentIndex = this.currentPageIndex.value;
    if (currentIndex < this.pages.length - 1) {
      this.currentPageIndex.next(currentIndex + 1);
    }
  }

  /**
   * Passe à la page précédente
   */
  previousPage(): void {
    const currentIndex = this.currentPageIndex.value;
    if (currentIndex > 0) {
      this.currentPageIndex.next(currentIndex - 1);
    }
  }

  /**
   * Va à une page spécifique par index
   * @param index L'index de la page (0-based)
   */
  goToPage(index: number): void {
    if (index >= 0 && index < this.pages.length) {
      this.currentPageIndex.next(index);
    }
  }

  /**
   * Va à une page spécifique par ID
   * @param id L'ID de la page (1-based)
   */
  goToPageById(id: number): void {
    const index = this.pages.findIndex(p => p.id === id);
    if (index !== -1) {
      this.currentPageIndex.next(index);
    }
  }

  /**
   * Vérifie si on est à la première page
   */
  isFirstPage(): boolean {
    return this.currentPageIndex.value === 0;
  }

  /**
   * Vérifie si on est à la dernière page
   */
  isLastPage(): boolean {
    return this.currentPageIndex.value === this.pages.length - 1;
  }

  /**
   * Récupère le nombre total de pages
   */
  getTotalPages(): number {
    return this.pages.length;
  }

  /**
   * Ajoute une nouvelle page
   * @param page La page à ajouter
   */
  addPage(page: Omit<Page, 'id'>): void {
    const newPage: Page = {
      ...page,
      id: this.pages.length + 1
    };
    this.pages.push(newPage);
  }

  /**
   * Met à jour une page existante
   * @param id L'ID de la page
   * @param updates Les modifications à appliquer
   */
  updatePage(id: number, updates: Partial<Omit<Page, 'id'>>): void {
    const index = this.pages.findIndex(p => p.id === id);
    if (index !== -1) {
      this.pages[index] = { ...this.pages[index], ...updates };
    }
  }

  /**
   * Supprime une page par ID
   * @param id L'ID de la page à supprimer
   */
  deletePage(id: number): void {
    const index = this.pages.findIndex(p => p.id === id);
    if (index !== -1) {
      this.pages.splice(index, 1);
      // Re-numéroter les pages
      this.pages.forEach((page, i) => page.id = i + 1);
      
      // Ajuster la page courante si nécessaire
      const currentIndex = this.currentPageIndex.value;
      if (currentIndex >= this.pages.length) {
        this.currentPageIndex.next(Math.max(0, this.pages.length - 1));
      }
    }
  }

  /**
   * Réinitialise à la première page
   */
  resetToFirstPage(): void {
    this.currentPageIndex.next(0);
  }
}