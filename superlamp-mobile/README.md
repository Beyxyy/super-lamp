# SuperLamp Mobile

Application Android de suivi d'entraînement, construite à partir du backend `superLamp` (Kotlin/Spring).
Hors ligne, sans backend : les données sont stockées dans une base SQLite locale (Room).
L'interface est en Jetpack Compose (Material 3).

## Fonctionnalités

- **Accueil** : bouton vers la prochaine séance prévue (la séance qui suit la dernière réalisée dans le programme, en boucle), reprise d'une séance en cours, séance libre, statistiques de la semaine et dernières séances.
- **Programmes** : création des programmes (`Split`), de leurs séances types (`Workout`, ordonnées) et des exercices planifiés (`WorkoutExercise` : séries, reps et repos visés).
- **Séance** (`Lift`) : saisie rapide des séries, pré-remplies avec la série précédente ou la dernière séance (« Dernière fois : 8×60 · 8×60 »), minuteur de repos, durée, volume.
- **Exercices** : catalogue pré-rempli, recherche, filtre par groupe musculaire, historique, record, 1RM estimé et courbe de progression.
- **Historique** : séances groupées par mois.
- **Import texte** : reprise du `ParserService` du backend pour coller des notes du type :

  ```
  27/06:
  Élévation poulie : 5*30 5*30 15*20
  Reverse fly : 8*2 7*2 6*2
  ```

## Correspondance avec le backend

| Backend (`superlamp-core` / `superlamp-data`) | Mobile |
|---|---|
| Tables `split`, `workout`, `exercise`, `workout_exercise`, `lift`, `lift_set` | Mêmes tables et colonnes (`data/entity/Entities.kt`) |
| `*RepositoryPort` + adapters JPA | DAO Room (`data/dao/Daos.kt`) |
| `*CoreService` (règles `require`) | Repositories (`data/repository`) |
| `ParserService` + `ParserServiceTest` | `core/ParserService.kt` + tests portés |

Écarts volontaires :

- pas de `user_id` (application mono-utilisateur) ;
- supprimer une séance type ou un exercice planifié ne supprime plus l'historique (`ON DELETE SET NULL`) ;
- `lift_set` référence directement l'exercice, ce qui permet les séances libres et l'import texte ;
- le parser complète l'année de la date `jj/mm`. Côté backend, `LocalDate.parse("12/05", ofPattern("dd/MM"))` lève une exception faute d'année, donc les tests `ParserServiceTest` du backend échouent.

## Lancer le projet

1. Ouvrir le dossier `superlamp-mobile` dans Android Studio (*File > Open*).
2. Laisser la synchronisation Gradle télécharger les dépendances.
3. Lancer la configuration `app` sur un émulateur ou un téléphone (Android 8.0 / API 26 minimum).

En ligne de commande, Gradle peut utiliser le JDK fourni avec Android Studio :

```bash
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat assembleDebug
```

L'APK est généré dans `app/build/outputs/apk/debug/app-debug.apk`.

Tests unitaires (parser et calcul de la prochaine séance) :

```bash
./gradlew testDebugUnitTest
```

## Stack

Gradle 9.8, AGP 9.4 (Kotlin intégré), Kotlin 2.4.20, Compose BOM 2026.09, Material 3, Navigation Compose 2.10, Room 2.8 (KSP 2.3), compileSdk 37, minSdk 26.
