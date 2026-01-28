describe('Post Creation E2E Tests', () => {
  let testUser: any;

  beforeEach(() => {
    // 1. Création d'un utilisateur unique pour le test
    const id = Math.floor(Math.random() * 1000000);
    testUser = {
      pseudo: `writer_${id}`,
      email: `writer_${id}@test.fr`,
      mdp: 'test123!'
    };

    // 2. Inscription
    cy.visit('/register');
    cy.get('input[formControlName="email"]').type(testUser.email);
    cy.get('input[formControlName="pseudo"]').type(testUser.pseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // 3. Connexion
    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(testUser.email);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // 4. On s'abonne à un thème (obligatoire pour voir le feed après)
    cy.get('a[routerLink="/themes"]:visible').click();
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();

    // 5. Navigation vers la page de création
    cy.visit('/articles/create');
  });

  it('should display the creation form correctly', () => {
    cy.get('h1').contains('Créer un nouvel article').should('be.visible');
    cy.get('select[formControlName="topicId"]').should('be.visible');
    cy.get('input[formControlName="titre"]').should('be.visible');
    cy.get('textarea[formControlName="contenu"]').should('be.visible');
    cy.get('button[type="submit"]').should('be.disabled'); // Désactivé car vide
  });

  it('should create a new post and redirect to articles list', () => {
    const postTitle = "Mon article Cypress " + Date.now();
    const postContent = "Ceci est un contenu généré automatiquement pour tester la création d'articles.";

    // 1. Sélectionner le premier thème de la liste
    // On attend que les thèmes soient chargés (venant du data.sql)
    cy.get('select[formControlName="topicId"]').select(1); // Sélectionne la 2ème option (la 1ère est le placeholder)

    // 2. Remplir le titre et le contenu
    cy.get('input[formControlName="titre"]').type(postTitle);
    cy.get('textarea[formControlName="contenu"]').type(postContent);

    // 3. Le bouton doit être activé maintenant
    cy.get('button[type="submit"]').should('not.be.disabled').click();

    // 4. Vérifier la redirection vers /articles
    cy.url().should('include', '/articles');

    // 5. Vérifier que l'article est présent dans la liste
    cy.get('.post-card').contains(postTitle).should('be.visible');
    cy.get('.post-card').first().within(() => {
        cy.get('.author').should('contain', testUser.pseudo);
    });
  });
});