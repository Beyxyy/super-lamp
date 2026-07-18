# **1. Travel Diary **
Une application pour partager ses voyages à ses proches


### **Frontent** 
- **Langage** : Typescript
- **Framework** : Angular 20+

## **2. Conventions de Code et Style**
#### **Nommage**
- **Variables** : `snake_case` (ex: `user_name`)
- **Fonctions/Méthode** : `snake_case` (ex: `getUserName`)
- **Classes** : `PascalCase` (ex: `UserService`)
- **Constantes** : `UPPER_SNAKE_CASE` (ex: `MAX_RETRIES`)
#### Bonne pratique angular
- **Input** privilégier les Signals par rapport au @Input et @Output
- **Template** utiliser @for et @if et @else au lieu de ngIf ngFor, etc...
#### Architecture:
- Toujours mettre au bon endroit les fichiers (interfaces, services, Enum). Pas d'interface/enum dans les fichiers de composants

## **Project structure**
app
    ├── core
    │   ├── data-access
    │   ├── guards
    │   ├── interceptors
    │   ├── interface
    │   └── services (core service used across application)
    └── features
        └──── ** page name **
            ├── component **used in feature**
            ├── services (feature services used only in this page rarely used)
            ├── routes used un the feature
            └── enum **used in the feature**
    ├── routes
    └── shared
        ├── data-access
            ├──repository (appel api et caching)
            └──facade (interface entre les repository + logique métier lié et les composants)
        ├── directives
        ├── enums
        ├── pipes
        └── ui
            ├── layout (layout réutilisé dans l'application (grid, carousel full-page, pop-up etc...))
            ├── button
            │   └── button-component
            └── toast

