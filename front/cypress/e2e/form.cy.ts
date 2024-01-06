/// <reference types="cypress" />
import '../../cypress/support/commands';

describe('Session creation', () => {
  before(() => {
    cy.login();

    // Intercept the GET request for fetching teachers
    cy.fixture('teachers.json').then((teachers) => {
      cy.intercept('GET', '/api/teacher', { body: teachers }).as('getTeachers');
    });

    // Intercept the POST request for creating a session
    cy.fixture('session-create.json').then((sessionCreateData) => {
      cy.intercept('POST', '/api/session', { body: sessionCreateData }).as('postCreateSession');
    });

    // Intercept the GET request for fetching updated Sessions list
    cy.fixture('sessions-new-list.json').then((sessionListUpdate) => {
      cy.intercept('GET', '/api/session', { body: sessionListUpdate }).as('getSessionList');
    });
  });

  it('should create a new session', () => {
    cy.get('button[routerLink="create"]').click();

    cy.wait('@getTeachers');

    // Verify if content structure of Create page is correct
    cy.get('.create').should('exist');
    cy.get('.create mat-card-title').contains('Create session').should('exist');
    cy.get('.create mat-card-content form').should('exist');

    // Fill in the form with valid data
    const validFormData = {
      name: 'Session created for testing',
      date: '2024-03-11',
      teacher_id: 1,
      users: null,
      description: 'Maecenas tincidunt convallis odio, ac eleifend risus fringilla vitae. Nulla facilisi. Proin elementum erat libero, lacinia vulputate lectus pulvinar non. Sed faucibus lectus aliquet rhoncus aliquet. Integer nisl turpis, semper in eros in, condimentum efficitur elit. Suspendisse a imperdiet lacus.',
    };

    cy.get('input[formControlName=name]').type(validFormData.name);
    cy.get('input[formControlName=date]').type(validFormData.date);

    // Select the teacher from the mocked list
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Margot DELAHAYE').click();

    cy.get('textarea[formControlName=description]').type(validFormData.description);

    cy.get('button[type=submit]').click();
    cy.wait('@postCreateSession');
    cy.wait('@getSessionList');

    // Verify redirection to /sessions page
    cy.url().should('include', '/sessions');

    // Verify if the newly created Session is displayed in the Session list
    cy.contains('.items .item', validFormData.name).should('exist');
  });
});
