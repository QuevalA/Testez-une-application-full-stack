// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

/// <reference types="cypress" />

declare global {
  namespace Cypress {
    interface Chainable {
      login(isAdmin?: boolean): Chainable<void>;
    }
  }
}

export {};

Cypress.Commands.add('login', (isAdmin = true) => {
  // Intercept the POST request for login
  cy.intercept('POST', '/api/auth/login', {
    body: {
      id: 1,
      username: 'userName',
      firstName: 'firstName',
      lastName: 'lastName',
      admin: isAdmin,
    },
  }).as('postLogin');

  // Intercept the GET request for the session list
  cy.intercept(
    {
      method: 'GET',
      url: '/api/session',
    },
    {
      fixture: 'sessions.json',
    }
  ).as('getSession');

  // Start from the home page
  cy.visit('/');

  // Click on Login
  cy.contains('Login').click();

  // Fill the login form with valid data
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');

  cy.get('button[type=submit]').click();

  // Wait for both login and session requests to complete
  cy.wait(['@postLogin', '@getSession']);

  // Ensure the URL is now on the sessions page
  cy.url().should('include', '/sessions');

  // Extract and use the "admin" value from the response body
  cy.get('@postLogin').its('response.body.admin').as('isAdmin');
});

