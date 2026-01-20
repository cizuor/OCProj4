describe('Post Detail and Comments E2E Tests', () => {
  let testUser: any;
  let postId: number;

  beforeEach(() => {
    // 1. Création d'un utilisateur unique
    const id = Math.floor(Math.random() * 1000000);
    testUser = {
      pseudo: `user_${id}`,
      email: `test_${id}@test.fr`,
      mdp: 'test123!'
    };

    // 2. Inscription & Login
    cy.visit('/register');
    cy.get('input[formControlName="email"]').type(testUser.email);
    cy.get('input[formControlName="pseudo"]').type(testUser.pseudo);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    cy.visit('/login');
    cy.get('input[formControlName="login"]').type(testUser.email);
    cy.get('input[formControlName="password"]').type(testUser.mdp);
    cy.get('button[type="submit"]').click();

    // 3. S'abonner pour voir les articles (et en choisir un)
    cy.visit('/themes');
    cy.get('app-topic-card').first().find('button').contains("S'abonner").click();
    
    // 4. Aller sur le feed et cliquer sur le premier article
    cy.visit('/articles');
    cy.get('.post-card').first().find('button').click(); // "Lire la suite"
    
    // On récupère l'ID depuis l'URL pour les vérifications si besoin
    cy.url().then(url => {
      const parts = url.split('/');
      postId = Number(parts[parts.length - 1]);
    });
  });

  it('should display the post content correctly', () => {
    cy.get('.post-full').should('be.visible');
    cy.get('.post-header h1').should('not.be.empty');
    cy.get('.post-meta .author').should('contain', 'par');
    cy.get('.post-body').should('not.be.empty');
  });

  it('should allow adding a new comment and see it in the list', () => {
    const commentText = "Ceci est un commentaire test par Cypress à " + Date.now();

    // 1. On vérifie le nombre de commentaires avant
    cy.get('.comments-section').then($section => {
      const initialCount = $section.find('.comment-item').length;

      // 2. Taper et envoyer le commentaire
      cy.get('textarea[formControlName="contenu"]').type(commentText);
      cy.get('button.btn-send').should('not.be.disabled').click();

      // 3. Vérifier que le formulaire est vidé
      cy.get('textarea[formControlName="contenu"]').should('have.value', '');

      // 4. Vérifier que le nouveau commentaire apparaît dans la liste
      cy.get('.comment-item').should('have.length', initialCount + 1);
      cy.get('.comment-item').last().within(() => {
        cy.get('.comment-author').should('contain', testUser.pseudo);
        cy.get('.comment-content').should('contain', commentText);
      });
    });
  });

  it('should disable send button when comment is too long or empty', () => {
    // Cas vide
    cy.get('textarea[formControlName="contenu"]').clear();
    cy.get('button.btn-send').should('be.disabled');

    // Cas trop long (> 500 carac)
    const longText = "a".repeat(501);
    cy.get('textarea[formControlName="contenu"]').type(longText, { delay: 0 });
    cy.get('button.btn-send').should('be.disabled');
  });
});