import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ActionComment e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load ActionComments', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('ActionComment').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details ActionComment page', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('actionComment');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create ActionComment page', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionComment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit ActionComment page', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('ActionComment');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of ActionComment', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionComment');

    cy.get(`[data-cy="comment"]`)
      .type('Massachusetts revolutionary', { force: true })
      .invoke('val')
      .should('match', new RegExp('Massachusetts revolutionary'));

    cy.setFieldSelectToLastOfEntity('action');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of ActionComment', () => {
    cy.intercept('GET', '/api/action-comments*').as('entitiesRequest');
    cy.intercept('GET', '/api/action-comments/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/action-comments/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-comment');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('actionComment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/action-comments*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('action-comment');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
