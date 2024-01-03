/// <reference types="cypress" />

import '../../cypress/support/commands';

describe('Me Component', () => {
  before(() => {
    cy.login();
  });

  it('should display user information', () => {
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'test@example.com',
        lastName: 'Doe',
        firstName: 'John',
        admin: false,
        password: 'hashedPassword', // Include password only if needed for testing
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('getUser');

    cy.contains('Account').click();
    cy.wait('@getUser');

    // Check if user information is displayed correctly
    cy.get('h1').should('contain.text', 'User information');
    cy.get('p:contains("Name:")').should('exist');
    cy.get('p:contains("Email:")').should('exist');
    cy.get('p:contains("Create at:")').should('exist');
    cy.get('p:contains("Last update:")').should('exist');
  });
});
