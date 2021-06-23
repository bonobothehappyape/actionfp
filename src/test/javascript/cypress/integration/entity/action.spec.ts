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

describe('Action e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load Actions', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Action').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Action page', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('action');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Action page', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Action');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Action page', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Action');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Action', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Action');

    cy.get(`[data-cy="taskName"]`)
      .type('invoice cross-platform Cove', { force: true })
      .invoke('val')
      .should('match', new RegExp('invoice cross-platform Cove'));

    cy.get(`[data-cy="taskDescription"]`)
      .type('back-end Buckinghamshire', { force: true })
      .invoke('val')
      .should('match', new RegExp('back-end Buckinghamshire'));

    cy.get(`[data-cy="requiresPeriodicFollowup"]`).should('not.be.checked');
    cy.get(`[data-cy="requiresPeriodicFollowup"]`).click().should('be.checked');

    cy.get(`[data-cy="initialDeadline"]`).type('2021-06-23').should('have.value', '2021-06-23');

    cy.get(`[data-cy="updatedDeadline"]`).type('2021-06-23').should('have.value', '2021-06-23');

    cy.get(`[data-cy="doneDate"]`).type('2021-06-23').should('have.value', '2021-06-23');

    cy.get(`[data-cy="verifiedDate"]`).type('2021-06-23').should('have.value', '2021-06-23');

    cy.setFieldSelectToLastOfEntity('icsRecomm');

    cy.setFieldSelectToLastOfEntity('ownerUnit');

    cy.setFieldSelectToLastOfEntity('status');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/actions*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Action', () => {
    cy.intercept('GET', '/api/actions*').as('entitiesRequest');
    cy.intercept('GET', '/api/actions/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/actions/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('action').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/actions*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('action');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
