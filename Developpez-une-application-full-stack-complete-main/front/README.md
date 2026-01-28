# MddClient

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 20.x.x.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.





# MDD - Monde de Dév

MDD est une application Full Stack de partage de contenu technique conçue exclusivement pour les développeurs. Elle permet aux utilisateurs de réaliser une veille technologique personnalisée en s'abonnant à des thématiques précises, de consulter un fil d'actualités dynamique et d'échanger avec la communauté via des commentaires.

## 🚀 Fonctionnalités

- **Authentification sécurisée** : Système d'inscription et de connexion basé sur des jetons **JWT** (Stateless).
- **Flux d'actualités personnalisé (Feed)** : Un fil d'articles trié dynamiquement (plus récents / plus anciens) basé uniquement sur les abonnements de l'utilisateur.
- **Gestion des thèmes (Topics)** : Liste complète des sujets techniques avec système d'abonnement et de désabonnement en temps réel.
- **Publications** : Création d'articles simplifiée incluant titre, contenu et thématique associée.
- **Interactions** : Système de commentaires sous chaque article pour favoriser l'échange technique.
- **Profil Utilisateur** : Gestion complète des informations personnelles (pseudo, email) et mise à jour sécurisée du mot de passe.
- **Interface Responsive** : Design moderne optimisé pour une utilisation fluide sur Desktop, Tablette et Mobile.

---

## 🛠 Stack Technique

### Backend (API REST)
- **Langage** : Java 21
- **Framework** : Spring Boot 3.4.1
- **Sécurité** : Spring Security 6 + JJWT 0.12.5
- **Persistance** : Spring Data JPA / Hibernate 6
- **Base de données** : H2 (Développement & Tests) / MySQL (Production)
- **Qualité** : SonarLint, Lombok, JaCoCo

### Frontend (SPA)
- **Framework** : Angular 18 (Standalone Components)
- **Gestion d'état** : RxJS & BehaviorSubjects (Programmation réactive)
- **Formulaires** : Reactive Forms avec validation stricte côté client
- **Style** : CSS Moderne (Flexbox & CSS Grid)
- **Navigation** : Système de Layouts et Guards de sécurité

---

## 📦 Installation et Lancement

### Prérequis
- **Java 21** installé
- **Node.js** (v20 ou v22 recommandés)
- **Maven 3.x**
- **Angular CLI**

### 1. Configuration du Backend
```bash
# Aller dans le dossier back
cd back

# Installation des dépendances et lancement
mvn spring-boot:run 
```

le lancement se fait aussi via docker 
```bash
#crée / lance la bdd et le backend 
docker-compose up -d
```

1. se connecter à la base de données. Tapez la commande ci-dessous

    ```
    mysql -u user_test -p
    ```
    L'invite de commande demandera le mot de passe. Il est : ```test_password```.

2. Se connecter au schéma de base de données `mdd_db`. Dans l'invite de commande, tapez la commande ci-dessous :

```
use mdd_db;
```


### 2. Configuration du Frontend
```bash
# Aller dans le dossier front
cd front

# Installer les dépendances
npm install

# Lancer le serveur de développement
ng serve
```


### Stratégie de Tests et Couverture


## Backend (Tests Unitaires & Intégration)

Les tests sont réalisés avec JUnit 5 et Mockito. La base de données H2 assure l'isolation des tests d'intégration.

# Lancer l'intégralité des tests Java avec le rapport de couverture JaCoCo
mvn clean test jacoco:report

## Frontend & E2E (Cypress)

# Lancer les tests Cypress en mode console
cd front
npx cypress run


# Lancer les tests Cypress avec le coverage
npx ng run mdd-client:serve-coverage



### Structure du projet
```text
├── back/                      # API Spring Boot
│   ├── src/main/java/         # Logique métier (Controller, Service, Repository, DTO)
│   ├── src/main/resources/    # Configuration de production (MySQL)
│   ├── src/test/java/         # Tests JUnit & Classe de lancement "TestMddApiApplication"
│   └── src/test/resources/    # Configuration de test/dev (H2) et scripts SQL
├── front/                     # Application Angular
│   ├── src/app/core/          # Services globaux, Guards, Interceptors, Interfaces
│   ├── src/app/features/      # Composants par domaine (Auth, Post, Topic)
│   ├── src/app/shared/        # Composants réutilisables
│   └── cypress/               # Scénarios de tests E2E
└── README.md
```


### EndPoint backend
```text

#Authentification
/api/auth/register : POST : Inscription d'un nouvel utilisateur
/api/auth/login : POST : Authentifie un utilisateur

#Utilisateur
/api/utilisateur/me : GET : Récupère les informations du profil connecté
/api/utilisateur/{id}: GET : Récupère les informations publiques d'un utilisateur
/api/utilisateur/subscribe/{topicId} : POST : Abonne l'utilisateur à un thème spécifique
/api/utilisateur/unsubscribe/{topicId} : POST :Désabonne l'utilisateur d'un thème spécifique
/api/utilisateur/me : PUT : Met à jour le profil (pseudo, email, mot de passe)

#Thèmes
/api/topic/{id} : GET : Récupère le détail d'un thème
/api/topic : GET : Récupère la liste de tous les thèmes
/api/topic/suivie : GET : Liste des thèmes suivis par l'utilisateur
/api/topic : POST : Création du topic

#Articles
/api/articles/{id} : GET : Récupère le détail d'un article
/api/articles/feed : GET : Récupère le flux personnalisé (abonnements)
/api/articles : GET : Récupère la liste globale de tous les articles
/api/articles : POST : Publication d'un nouvel article
/api/articles/{id} : PUT : Modifie un article existant (auteur uniquement)
/api/articles/{id} : DELETE : Supprime un article (auteur uniquement)

#Commentaires
/api/commentaire/{id} : GET : Récupère le détail d'un commentaire
/api/commentaire/article/{id} : GET : Récupère les commentaires d'un article
/api/commentaire/article/{id} : POST : Ajoute un commentaire sur un article

```