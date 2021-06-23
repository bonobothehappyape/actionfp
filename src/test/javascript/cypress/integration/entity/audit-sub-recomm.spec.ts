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

describe('AuditSubRecomm e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load AuditSubRecomms', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('AuditSubRecomm').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details AuditSubRecomm page', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('auditSubRecomm');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create AuditSubRecomm page', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('AuditSubRecomm');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit AuditSubRecomm page', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('AuditSubRecomm');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of AuditSubRecomm', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('AuditSubRecomm');

    cy.get(`[data-cy="subRecommNum"]`)
      .type('Advanced Buckinghamshire Neck', { force: true })
      .invoke('val')
      .should('match', new RegExp('Advanced Buckinghamshire Neck'));

    cy.get(`[data-cy="description"]`)
      .type('hacking Sausages', { force: true })
      .invoke('val')
      .should('match', new RegExp('hacking Sausages'));

    cy.setFieldSelectToLastOfEntity('status');

    cy.setFieldSelectToLastOfEntity('auditRecomm');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of AuditSubRecomm', () => {
    cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequest');
    cy.intercept('GET', '/api/audit-sub-recomms/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/audit-sub-recomms/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-sub-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('auditSubRecomm').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/audit-sub-recomms*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('audit-sub-recomm');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
