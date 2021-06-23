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

describe('AuditRecomm e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load AuditRecomms', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('AuditRecomm').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details AuditRecomm page', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('auditRecomm');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create AuditRecomm page', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('AuditRecomm');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit AuditRecomm page', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('AuditRecomm');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of AuditRecomm', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('AuditRecomm');

    cy.get(`[data-cy="recommNumber"]`)
      .type('payment Programmable', { force: true })
      .invoke('val')
      .should('match', new RegExp('payment Programmable'));

    cy.get(`[data-cy="priority"]`).type('10201').should('have.value', '10201');

    cy.get(`[data-cy="description"]`).type('matrices Music', { force: true }).invoke('val').should('match', new RegExp('matrices Music'));

    cy.setFieldSelectToLastOfEntity('report');

    cy.setFieldSelectToLastOfEntity('status');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of AuditRecomm', () => {
    cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequest');
    cy.intercept('GET', '/api/audit-recomms/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/audit-recomms/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('auditRecomm').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/audit-recomms*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('audit-recomm');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
