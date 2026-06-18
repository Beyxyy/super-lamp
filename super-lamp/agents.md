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

## **Project structure**
app
    ├── core
    │   ├── data-access
    │   ├── guards
    │   ├── interceptors
    │   ├── interface
    │   └── services (core service used across application)
    ├── features
    │   ├── **feature name **
    │   │   ├── component **feature component (used in page)**
    │   │   ├── pages (feature pages)
    │   │   └── services (feature services)
    ├── routes
    └── shared
        ├── data-access
        ├── directives
        ├── pipes
        └── ui
            ├── button
            │   └── button-component
            └── toast

