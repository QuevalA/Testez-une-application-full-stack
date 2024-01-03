import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

import { TeacherService } from './teacher.service';
import {Teacher} from "../interfaces/teacher.interface";

describe('TeacherService', () => {
  let service: TeacherService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService],
    });

    service = TestBed.inject(TeacherService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should make a GET request to retrieve all teachers', () => {
    const expectedTeachers = [{ id: '1', name: 'Teacher 1' }, { id: '2', name: 'Teacher 2' }];

    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(expectedTeachers);
    });

    const req = httpTestingController.expectOne('api/teacher');
    expect(req.request.method).toEqual('GET');
    req.flush(expectedTeachers);
  });

  it('should make a GET request to retrieve teacher by ID', () => {
    const teacherId = '1';
    const expectedTeacher = { id: teacherId, name: 'Teacher 1' };

    service.detail(teacherId).subscribe((teacher) => {
      expect(teacher).toEqual(expectedTeacher);
    });

    const req = httpTestingController.expectOne(`api/teacher/${teacherId}`);
    expect(req.request.method).toEqual('GET');
    req.flush(expectedTeacher);
  });

  it('should handle errors when retrieving all teachers', () => {
    service.all().subscribe(
      () => fail('should have failed with an error'),
      (error) => {
        expect(error).toBeTruthy();
      }
    );

    const req = httpTestingController.expectOne('api/teacher');
    req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
  });

  it('should handle an empty array when retrieving all teachers', () => {
    const expectedTeachers: Teacher[] = [];

    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(expectedTeachers);
    });

    const req = httpTestingController.expectOne('api/teacher');
    req.flush(expectedTeachers);
  });
});
