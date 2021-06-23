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

describe('ActionChangeMail e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load ActionChangeMails', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('ActionChangeMail').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details ActionChangeMail page', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('actionChangeMail');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create ActionChangeMail page', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionChangeMail');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit ActionChangeMail page', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('ActionChangeMail');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of ActionChangeMail', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ActionChangeMail');

    cy.get(`[data-cy="actionType"]`)
      .type('Decentralized Lesotho', { force: true })
      .invoke('val')
      .should('match', new RegExp('Decentralized Lesotho'));

    cy.setFieldSelectToLastOfEntity('action');

    cy.setFieldSelectToLastOfEntity('user');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of ActionChangeMail', () => {
    cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequest');
    cy.intercept('GET', '/api/action-change-mails/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/action-change-mails/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('action-change-mail');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('actionChangeMail').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/action-change-mails*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('action-change-mail');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
