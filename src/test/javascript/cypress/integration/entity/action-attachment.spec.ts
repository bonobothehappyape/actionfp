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

describe('ActionAttachment e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load ActionAttachments', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('ActionAttachment').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details ActionAttachment page', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('actionAttachment');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create ActionAttachment page', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionAttachment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit ActionAttachment page', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('ActionAttachment');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of ActionAttachment', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionAttachment');

    cy.get(`[data-cy="name"]`)
      .type('Rubber Inlet Centralized', { force: true })
      .invoke('val')
      .should('match', new RegExp('Rubber Inlet Centralized'));

    cy.get(`[data-cy="mimeType"]`).type('Small', { force: true }).invoke('val').should('match', new RegExp('Small'));

    cy.setFieldImageAsBytesOfEntity('attachedFile', 'integration-test.png', 'image/png');

    cy.get(`[data-cy="url"]`)
      .type('https://stanley.name', { force: true })
      .invoke('val')
      .should('match', new RegExp('https://stanley.name'));

    cy.setFieldSelectToLastOfEntity('action');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of ActionAttachment', () => {
    cy.intercept('GET', '/api/action-attachments*').as('entitiesRequest');
    cy.intercept('GET', '/api/action-attachments/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/action-attachments/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-attachment');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('actionAttachment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/action-attachments*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('action-attachment');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
