import { Component, Input, Output, EventEmitter, inject, DestroyRef, OnInit, ViewChild, ElementRef, input, output } from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import { PageService } from '@shared/data-access/facade/page.service';
import { Page } from '@shared/entity/page.entity';
import { MODE, PageDirection } from '@pages/home-page/enum/page-direction.enum';
import { MarkdownComponent } from 'ngx-markdown';
import { UserService } from '@shared/data-access/facade/user.service';

@Component({
  selector: 'app-page',
  standalone: true,
  imports: [ MarkdownComponent],
  templateUrl: './page.component.html',
  styleUrl: './page.component.css'
})
export class PageComponent implements OnInit {
  @Input() page!: Page;
  e_turnPage = output<PageDirection>();

  @ViewChild("contenuMarkdown", { static: false  }) contenuRef!: ElementRef;

  public readonly MODE_READ = MODE.READ
  public readonly MODE_WRITE = MODE.WRITE

  public is_passed = false;
  public is_current = false;
  public mode = this.MODE_READ;

  public working_content = ""

  private destroyRef = inject(DestroyRef);

  constructor(private s_page: PageService, public s_user : UserService) {}

  ngOnInit(){
    this.s_page.currentPageIndex$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((current_page) => {
      this.computedClasses(current_page)
    });
  }


  private computedClasses(current_page: number) {
    this.is_current = this.page.id === current_page;
    this.is_passed = current_page > this.page.id;
  }


  get isLast(): boolean {
    return this.s_page.isLastPage();
  }

  onTurnPageLeft(): void {
    this.e_turnPage.emit(PageDirection.LEFT);
  }
   onTurnPageRight(): void {
    this.e_turnPage.emit(PageDirection.RIGHT);
  }

  onChangeMode($event : MODE){
    this.mode = $event
    if(this.mode === this.MODE_WRITE)
      this.working_content = this.page.contenu
  }

  onSave(){
    const new_content = this.contenuRef.nativeElement.textContent
    this.page = this.s_page.updatePage(this.page.id, { contenu: new_content });
    this.onChangeMode(this.MODE_READ)
  }
}
