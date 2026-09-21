import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { Component } from '@angular/core';

@Component({ standalone: true, template: '' })
class DashboardStubComponent {}

describe('LoginComponent', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([
        { path: 'dashboard', component: DashboardStubComponent }
      ])]
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('does not submit incomplete credentials', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.componentInstance.submit();

    expect(fixture.componentInstance.form.touched).toBeTrue();
  });

  it('loads the authenticated session after login', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.form.setValue({ username: 'manager', password: 'secret' });

    component.submit();

    const loginRequest = http.expectOne('/api/auth/login');
    expect(loginRequest.request.method).toBe('POST');
    expect(loginRequest.request.body).toContain('username=manager');
    loginRequest.flush({});

    const sessionRequest = http.expectOne('/api/auth/session');
    sessionRequest.flush({ username: 'manager', roles: ['HR_MANAGER'] });
  });
});
