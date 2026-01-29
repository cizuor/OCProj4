# P6-Full-Stack-reseau-dev

## Front

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 14.1.3.

Don't forget to install your node_modules before starting (`npm install`).

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

### Where to start

As you may have seen if you already started the app, a simple home page containing a logo, a title and a button is available. If you take a look at its code (in the `home.component.html`) you will see that an external UI library is already configured in the project.

This library is `@angular/material`, it's one of the most famous in the angular ecosystem. As you can see on their docs (https://material.angular.io/), it contains a lot of highly customizable components that will help you design your interfaces quickly.

Note: I recommend to use material however it's not mandatory, if you prefer you can get rid of it.

Good luck!






### Test

pour lancer l'application en mode test/dev il faut utilisé le fichier TestMddApiApplication.java
Utilisateur de test:
email : test@test.fr
pseudo : test
mdp : test123!


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
```bash
#lance le serveur angular mais pret pour le coverage
npx ng run mdd-client:serve-coverage  

#dans un autre terminal lancer les test
npx cypress run
```

puis fichier dans front/coverage/lcov-report/index.html



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

le plus simple est 
http://localhost:9000/swagger-ui/index.html#/
quand le serveur est en route
sinon voici la liste
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


### Github

https://github.com/cizuor/OCProj4