describe('User Profile E2E Tests', () => {
    let testUser: any;

  beforeEach(() => {
    
    const uniqueId = Date.now();
    testUser = {
        pseudo: `user_${uniqueId}`,
        email: `test_${uniqueId}@test.fr`,
        mdp: 'test123!'
    };

    cy.visit('/register');
    cy.get('input[formControlName="email"]').type(testUser.email);
    cy.get('input[formControlName="pseudo"]').type(testUser.pseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();
    // Se connecter avec les identifiants
    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(testUser.email);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/articles');

    cy.get('a[routerLink="/themes"]').click();
    // On clique sur le premier bouton "S'abonner" disponible
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();
    // On attend que le bouton passe en "Déjà abonné" (ton bouton gris)
    cy.get('app-topic-card').first().find('button').should('be.disabled');

    // Vérifier qu'on est connecté et aller sur le profil
    cy.get('a[routerLink="/profil"]').click(); // Adapte le sélecteur si besoin
    cy.url().should('include', '/profil');
  });

  it('should display user information correctly', () => {
    // Vérifie que les champs sont pré-remplis avec les données du data.sql
    cy.get('input[formControlName="pseudo"]').should('have.value', testUser.pseudo);
    cy.get('input[formControlName="email"]').should('have.value', testUser.email);
  });

  it('should update user information successfully', () => {
    const newPseudo = 'UpdatedPseudo';
    const newEmail = 'updated@test.fr';

    // Modifier les champs
    cy.get('input[formControlName="pseudo"]').clear().type(newPseudo);
    cy.get('input[formControlName="email"]').clear().type(newEmail);

    // Cliquer sur sauvegarder
    cy.get('button.btn-save').click();

    // Vérifier que les données sont persistantes après un rechargement
    cy.reload();
    
    // reconnection 
    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(newPseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    cy.get('a[routerLink="/profil"]').click(); // Adapte le sélecteur si besoin
    cy.url().should('include', '/profil');


    cy.get('input[formControlName="pseudo"]').should('have.value', newPseudo);
    cy.get('input[formControlName="email"]').should('have.value', newEmail);
  });

  it('should display subscriptions and allow unsubscribing', () => {
    // On vérifie qu'il y a des abonnements (selon ton data.sql ou setup)
    // On suppose que l'utilisateur est abonné à au moins un thème
    cy.get('app-topic-card').should('exist');

    // On récupère le nombre initial de cartes
    cy.get('app-topic-card').then(cards => {
      const countBefore = cards.length;

      // Cliquer sur le bouton "Se désabonner" de la première carte
      // Note : on utilise .first() pour cibler la première
      cy.get('app-topic-card').first().find('button.btn-subscribe').click();

      // Vérifier que le nombre de cartes a diminué de 1
      cy.get('app-topic-card').should('have.length', countBefore - 1);
    });
  });

  it('should prevent saving if form is invalid', () => {
    // Vider le pseudo
    cy.get('input[formControlName="pseudo"]').clear();
    
    // Le bouton sauvegarder doit être désactivé
    cy.get('button.btn-save').should('be.disabled');
  });

});