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

describe('Framework e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load Frameworks', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Framework').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Framework page', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('framework');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Framework page', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Framework');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Framework page', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Framework');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Framework', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Framework');

    cy.get(`[data-cy="year"]`).type('22259').should('have.value', '22259');

    cy.get(`[data-cy="name"]`).type('virtual Industrial', { force: true }).invoke('val').should('match', new RegExp('virtual Industrial'));

    cy.get(`[data-cy="type"]`).type('24701').should('have.value', '24701');

    cy.get(`[data-cy="description"]`)
      .type('responsive Brunei', { force: true })
      .invoke('val')
      .should('match', new RegExp('responsive Brunei'));

    cy.setFieldSelectToLastOfEntity('unit');

    cy.setFieldSelectToLastOfEntity('action');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Framework', () => {
    cy.intercept('GET', '/api/frameworks*').as('entitiesRequest');
    cy.intercept('GET', '/api/frameworks/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/frameworks/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('framework');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('framework').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/frameworks*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('framework');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
