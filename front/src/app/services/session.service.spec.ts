import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import {SessionInformation} from "../interfaces/sessionInformation.interface";

describe('SessionService', () => {
  let service: SessionService;

  const fakeUser: SessionInformation = {
    token: 'fakeToken',
    type: 'user',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in and out successfully', () => {
    service.logIn(fakeUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(fakeUser);

    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit changes when logging in and out', () => {
    let isLoggedResult: boolean | undefined;

    service.$isLogged().subscribe((value) => (isLoggedResult = value));

    service.logIn(fakeUser);

    expect(isLoggedResult).toBe(true);

    service.logOut();

    expect(isLoggedResult).toBe(false);
  });
});
