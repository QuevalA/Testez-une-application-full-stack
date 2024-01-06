/// <reference types="cypress" />
import '../../cypress/support/commands';

describe('Me Component', () => {
  // Login as an admin user
  before(() => {
    cy.login();
  });

  it('should display user information for admin user', () => {
    // Intercept the GET request for an admin user
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'test@example.com',
        lastName: 'Doe',
        firstName: 'John',
        admin: true,
        password: 'hashedPassword',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('getAdminUser');

    cy.contains('Account').click();
    cy.wait('@getAdminUser');

    // Check if user information is displayed correctly for admin user
    cy.get('h1').should('contain.text', 'User information');
    cy.get('p:contains("Name:")').should('exist');
    cy.get('p:contains("Email:")').should('exist');
    cy.get('p:contains("Create at:")').should('exist');
    cy.get('p:contains("Last update:")').should('exist');

    cy.get('button:contains("Detail")').should('not.exist');

    cy.contains('Logout').click();
  });

  it('should display user information for non-admin user', () => {
    // Login as a non-admin user
    cy.login(false);

    // Intercept the GET request for a non-admin user
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        email: 'test@example.com',
        lastName: 'Doe',
        firstName: 'John',
        admin: false,
        password: 'hashedPassword',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('getUser');

    cy.contains('Account').click();
    cy.wait('@getUser');

    // Check if user information is displayed correctly for non-admin user
    cy.get('h1').should('contain.text', 'User information');
    cy.get('p:contains("Name:")').should('exist');
    cy.get('p:contains("Email:")').should('exist');
    cy.get('p:contains("Create at:")').should('exist');
    cy.get('p:contains("Last update:")').should('exist');
    cy.get('button:contains("Detail")').should('exist');
    cy.get('p:contains("You are admin")').should('not.exist');

    cy.contains('Logout').click();
  });
});
