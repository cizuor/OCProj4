describe('Topic List E2E Tests', () => {
  let testUser: any;

  beforeEach(() => {
    // Création d'un utilisateur unique pour isoler les tests
    const id = Math.floor(Math.random() * 1000000);
    testUser = {
      pseudo: `topic_user_${id}`,
      email: `topic_${id}@test.fr`,
      mdp: 'test123!'
    };

    //Inscription
    cy.visit('/register');
    cy.get('input[formControlName="email"]').type(testUser.email);
    cy.get('input[formControlName="pseudo"]').type(testUser.pseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // Connexion
    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(testUser.email);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // Navigation vers la page des thèmes
    cy.url().should('include', '/articles');
    cy.get('a[routerLink="/themes"]').click();
    cy.url().should('include', '/themes');
  });

  it('should display all topics from database', () => {
    // On vérifie que le titre de la page est présent
    cy.get('h3').contains('Liste themes').should('be.visible');

    // On vérifie que les cartes de thèmes sont chargées (celles du data.sql)
    // On attend qu'il y en ait au moins une
    cy.get('app-topic-card').should('have.length.at.least', 1);
    
    // On vérifie qu'un des thèmes par défaut est bien là (ex: Java)
    cy.get('app-topic-card').contains('Java').should('exist');
  });

  it('should allow subscribing to a topic', () => {
    // 1. Trouver un thème non suivi (bouton "S'abonner")
    // On prend le premier
    cy.get('app-topic-card').first().as('firstCard');

    cy.get('@firstCard').find('button').contains("S'abonner").should('exist');

    // 2. Cliquer sur s'abonner
    cy.get('@firstCard').find('button').contains("S'abonner").click();

    // 3. Vérifier que l'état change (le bouton doit devenir "Déjà abonné" et être désactivé)
    // Selon ton code TopicCardComponent précédent pour le mode liste
    cy.get('@firstCard').find('button').contains("Déjà abonné").should('be.disabled');
  });

  it('should maintain subscription status after page reload', () => {
    // 1. S'abonner
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();
    cy.get('app-topic-card').first().find('button').contains("Déjà abonné").should('be.disabled');

    // 2. Recharger la page
    cy.reload();

    // 3. Vérifier que le thème est toujours marqué comme "Déjà abonné"
    // Cela prouve que le TopicService.getAll() récupère bien le statut 'liked' du serveur
    cy.get('app-topic-card').first().find('button').contains("Déjà abonné").should('be.disabled');
  });
});