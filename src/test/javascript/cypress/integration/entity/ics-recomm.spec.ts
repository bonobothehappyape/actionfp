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

describe('ICSRecomm e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load ICSRecomms', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('ICSRecomm').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details ICSRecomm page', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('iCSRecomm');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create ICSRecomm page', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ICSRecomm');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit ICSRecomm page', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('ICSRecomm');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of ICSRecomm', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ICSRecomm');

    cy.get(`[data-cy="year"]`).type('73443').should('have.value', '73443');

    cy.get(`[data-cy="icsNumber"]`).type('Product', { force: true }).invoke('val').should('match', new RegExp('Product'));

    cy.get(`[data-cy="icsDescr"]`).type('invoice', { force: true }).invoke('val').should('match', new RegExp('invoice'));

    cy.get(`[data-cy="title"]`)
      .type('Barthelemy auxiliary Generic', { force: true })
      .invoke('val')
      .should('match', new RegExp('Barthelemy auxiliary Generic'));

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of ICSRecomm', () => {
    cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequest');
    cy.intercept('GET', '/api/ics-recomms/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/api/ics-recomms/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('ics-recomm');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('iCSRecomm').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/ics-recomms*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('ics-recomm');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
