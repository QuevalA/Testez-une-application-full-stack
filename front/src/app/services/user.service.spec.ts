import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id', () => {
    const userId = '123';
    const expectedUser = { id: userId, name: 'John Doe' };

    service.getById(userId).subscribe((user) => {
      expect(user).toEqual(expectedUser);
    });

    // Expect a single request to the API
    const req = httpTestingController.expectOne(`api/user/${userId}`);
    expect(req.request.method).toEqual('GET');

    // Respond with the mock user
    req.flush(expectedUser);
  });

  it('should delete user by id', () => {
    const userId = '123';

    service.delete(userId).subscribe();

    // Expect a single request to the API
    const req = httpTestingController.expectOne(`api/user/${userId}`);
    expect(req.request.method).toEqual('DELETE');

    // Respond with a successful deletion
    req.flush({});
  });



  it('should handle errors when getting user by id', () => {
    const userId = '123';

    service.getById(userId).subscribe(
      () => fail('should have failed with an error'),
      (error) => {
        expect(error).toBeTruthy();
      }
    );

    const req = httpTestingController.expectOne(`api/user/${userId}`);
    req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
  });

  it('should handle errors when deleting user by id', () => {
    const userId = '123';

    service.delete(userId).subscribe(
      () => fail('should have failed with an error'),
      (error) => {
        expect(error).toBeTruthy();
      }
    );

    const req = httpTestingController.expectOne(`api/user/${userId}`);
    req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
  });

  it('should handle an empty response when getting user by id', () => {
    const userId = '123';

    service.getById(userId).subscribe((user) => {
      expect(user).toBeNull();
    });

    const req = httpTestingController.expectOne(`api/user/${userId}`);
    req.flush({});
  });
});
