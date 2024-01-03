import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

import { AuthService } from './auth.service';
import {SessionInformation} from "../../../interfaces/sessionInformation.interface";
import {RegisterRequest} from "../interfaces/registerRequest.interface";

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a user successfully', () => {
    const registerRequest: RegisterRequest = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      password: 'password123',
    };

    const expectedResponse: { message: string } = { message: 'User registered successfully!' };

    service.register(registerRequest).subscribe((response) => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne('api/auth/register');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(registerRequest);

    req.flush(expectedResponse);
  });


  it('should login a user successfully', () => {
    const loginRequest = {
      email: 'john.doe@example.com',
      password: 'password123',
    };

    const expectedResponse: SessionInformation = {
      token: 'fakeToken',
      type: 'user',
      id: 1,
      username: 'john.doe',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };

    service.login(loginRequest).subscribe((response) => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne('api/auth/login');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(loginRequest);

    req.flush(expectedResponse);
  });
});
