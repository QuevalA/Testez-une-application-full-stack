/// <reference types="cypress" />
import '../../cypress/support/commands';

describe('Session Detail', () => {
  before(() => {
    cy.login();

    // Intercept the GET request for a specific session (session details)
    cy.intercept({
      method: 'GET',
      url: '/api/session/2',
    }, {
      fixture: 'session-details.json',
    }).as('sessionDetail');


  cy.intercept({
    method: 'GET',
    url: '/api/teacher/1',
  }, {
    fixture: 'teacher-details.json',
  }).as('teacherDetail');
});

  it('should navigate to session detail, display content, and go back', () => {
    cy.get('.item:first-child').as('firstSession');
    cy.get('@firstSession').contains('button', 'Detail').should('be.visible').click();
    cy.url().should('include', '/detail/');

    // Wait for the session detail request to complete
    cy.wait('@sessionDetail');
    cy.wait('@teacherDetail');

    // Assert the content on the detail page
    cy.get('h1').contains(/^Session fixture #1$/i);
    cy.get('.description').should('exist');

    cy.get('.mat-card-title button[mat-icon-button]').click();

    // Ensure the URL is back to the sessions page
    cy.url().should('include', '/sessions');
  });
});
