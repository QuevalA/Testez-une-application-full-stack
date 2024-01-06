/// <reference types="cypress" />
import '../../cypress/support/commands';

describe('Session List', () => {
  before(() => {
    cy.login();
  });

  it('should display the list page with proper content', () => {

    // Assert content header
    cy.get('.list mat-card-header').within(() => {
      cy.contains('Rentals available').should('exist');
      cy.get('button').should('exist').and('contain', 'Create');
    });

    // Assert the content of the list
    cy.get('.list').should('exist');

    // Verify the presence of 3 session cards
    cy.get('.items .item').should('have.length', 3).each(($sessionCard) => {

      // Assert basic structure within each session card
      cy.wrap($sessionCard).within(() => {
        cy.get('mat-card-title').should('exist');
        cy.get('mat-card-subtitle').should('exist');
        cy.get('.picture').should('exist');
        cy.get('mat-card-content p').should('exist');
        cy.get('mat-card-actions button').contains('Detail').should('exist');
        cy.get('mat-card-actions button').contains('Edit').should('exist');
      });
    });
  });
});
