describe('Post List (Feed) E2E Tests', () => {
  let testUser: any;

  beforeEach(() => {
    // 1. Création d'un utilisateur unique pour chaque test
    const id = Math.floor(Math.random() * 1000000);
    testUser = {
      pseudo: `writer_${id}`,
      email: `writer_${id}@test.fr`,
      mdp: 'test123!'
    };

    // 2. Inscription et Connexion
    cy.visit('/register');
    cy.get('input[formControlName="email"]').type(testUser.email);
    cy.get('input[formControlName="pseudo"]').type(testUser.pseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(testUser.email);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // On arrive sur la liste des articles
    cy.url().should('include', '/articles');
  });

  it('should show an empty message when the user has no subscriptions', () => {
    // Par défaut, un nouvel utilisateur n'a pas d'abonnements
    cy.contains("Aucun article à afficher. Abonnez-vous à des thèmes !").should('be.visible');
  });

  it('should navigate to the create post page', () => {
    // On clique sur le bouton de création
    cy.get('button.btn-create').click();
    
    // On vérifie que l'URL change vers la page de création
    cy.url().should('include', '/articles/create');
  });

  it('should display posts after subscribing to a topic', () => {
    // 1. Aller sur la page des thèmes pour s'abonner
    cy.get('a[routerLink="/themes"]:visible').click();
    
    // 2. S'abonner au premier thème (ex: Java dans ton data.sql)
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();

    // 3. Revenir sur la liste des articles
    cy.get('a[routerLink="/articles"]:visible').click();

    // 4. Vérifier qu'au moins un article est maintenant visible
    // (En supposant que ton data.sql contient un article pour le thème choisi)
    cy.get('.post-card').should('have.length.at.least', 1);
    
    // Vérifier les informations de la carte
    cy.get('.post-card').first().within(() => {
      // On vérifie le titre avec la classe .post-title
      cy.get('.post-title').should('not.be.empty');

      // On vérifie que l'auteur est présent (classe .author)
      cy.get('.author').should('not.be.empty');

      // On vérifie que le contenu est présent (classe .post-body)
      cy.get('.post-body').should('not.be.empty');
    });
  });

  it('should navigate to post details when clicking Read More', () => {
    // 1. On s'abonne pour avoir un article
    cy.get('a[routerLink="/themes"]:visible').click();
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();
    cy.get('a[routerLink="/articles"]:visible').click();

    // 2. On clique sur le bouton "Lire la suite"
    cy.get('.post-card').first().click();

    // 3. On vérifie qu'on arrive sur la page de détail (URL contient l'ID)
    cy.url().should('match', /\/articles\/\d+/);
  });
});