import { Page } from "@shared/entity/page.entity";
import { CrudService } from "./crud.service";
import { Injectable } from "@angular/core";

@Injectable({providedIn: "root"})
export class PageApiService extends CrudService<Page>{
    protected override entityName  = "page";
    
}