import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all sessions', () => {
    const expectedSessions: Session[] = [
      { id: 1, name: 'Session 1', description: 'Description 1', date: new Date(), teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date() },
      { id: 2, name: 'Session 2', description: 'Description 2', date: new Date(), teacher_id: 2, users: [], createdAt: new Date(), updatedAt: new Date() },
    ];

    service.all().subscribe((sessions) => {
      expect(sessions).toEqual(expectedSessions);
    });

    const req = httpTestingController.expectOne('api/session');
    expect(req.request.method).toEqual('GET');

    req.flush(expectedSessions);
  });

  it('should get session detail', () => {
    const sessionId = 1;
    const expectedSession: Session = { id: sessionId, name: 'Session 1', description: 'Description 1', date: new Date(), teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date() };

    service.detail(sessionId.toString()).subscribe((session) => {
      expect(session).toEqual(expectedSession);
    });

    const req = httpTestingController.expectOne(`api/session/${sessionId}`);
    expect(req.request.method).toEqual('GET');

    req.flush(expectedSession);
  });

  it('should delete session', () => {
    const sessionId = 1;

    service.delete(sessionId.toString()).subscribe();

    const req = httpTestingController.expectOne(`api/session/${sessionId}`);
    expect(req.request.method).toEqual('DELETE');

    req.flush({});
  });

  it('should create session', () => {
    const newSession: Session = { name: 'New Session', description: 'New Description', date: new Date(), teacher_id: 1, users: [] };

    service.create(newSession).subscribe((session) => {
      expect(session).toEqual(newSession);
    });

    const req = httpTestingController.expectOne('api/session');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(newSession);

    req.flush(newSession);
  });

  it('should update session', () => {
    const sessionId = 1;
    const updatedSession: Session = { id: sessionId, name: 'Updated Session', description: 'Updated Description', date: new Date(), teacher_id: 1, users: [] };

    service.update(sessionId.toString(), updatedSession).subscribe((session) => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpTestingController.expectOne(`api/session/${sessionId}`);
    expect(req.request.method).toEqual('PUT');
    expect(req.request.body).toEqual(updatedSession);

    req.flush(updatedSession);
  });

  it('should participate in session', () => {
    const sessionId = 1;
    const userId = 'user1';

    service.participate(sessionId.toString(), userId).subscribe();

    const req = httpTestingController.expectOne(`api/session/${sessionId}/participate/${userId}`);
    expect(req.request.method).toEqual('POST');

    req.flush({});
  });

  it('should unparticipate in session', () => {
    const sessionId = 1;
    const userId = 'user1';

    service.unParticipate(sessionId.toString(), userId).subscribe();

    const req = httpTestingController.expectOne(`api/session/${sessionId}/participate/${userId}`);
    expect(req.request.method).toEqual('DELETE');

    req.flush({});
  });
});
