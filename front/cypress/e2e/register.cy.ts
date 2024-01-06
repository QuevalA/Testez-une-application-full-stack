/// <reference types="cypress" />

describe('Registration', () => {
  it('should successfully register a new user', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: { message: 'User registered successfully!' },
    }).as('registrationRequest');

    cy.visit('/');
    cy.contains('Register').click();

    // Fill and submit the registration form with valid data
    cy.get('[formControlName=firstName]').type('John');
    cy.get('[formControlName=lastName]').type('Doe');
    cy.get('[formControlName=email]').type('john.doe@example.com');
    cy.get('[formControlName=password]').type('password123');

    cy.get('form').submit();

    cy.wait('@registrationRequest');

    // Ensure redirection to the login page after successful registration
    cy.url().should('include', '/login');
  });

  it('should display errors for invalid form inputs', () => {
    cy.visit('/');
    cy.contains('Register').click();

    cy.get('form').submit();

    // Ensure validation errors are displayed for required fields
    cy.get('[formControlName=firstName]').should('have.class', 'ng-invalid');
    cy.get('[formControlName=lastName]').should('have.class', 'ng-invalid');
    cy.get('[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('[formControlName=password]').should('have.class', 'ng-invalid');

    cy.get('[formControlName=email]').type('invalid-email');
    cy.get('[formControlName=password]').type('1111');

    cy.get('form').submit();

    // Ensure validation errors are displayed for specific fields
    cy.get('[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('[formControlName=password]').should('have.class', 'ng-invalid');

    // Ensure the user stays on the registration page (no redirection)
    cy.url().should('include', '/register');
  });

  it('should handle registration errors', () => {
    // Mock the registration request to return an error
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: { error: 'Internal Server Error' },
    }).as('registrationRequestError');

    cy.visit('/');
    cy.contains('Register').click();

    // Fill and submit the registration form with valid data
    cy.get('[formControlName=firstName]').type('John');
    cy.get('[formControlName=lastName]').type('Doe');
    cy.get('[formControlName=email]').type('john.doe@example.com');
    cy.get('[formControlName=password]').type('password123');

    cy.get('form').submit();

    cy.wait('@registrationRequestError');

    // Ensure the error message is displayed
    cy.get('.error').should('be.visible');
  });
});
