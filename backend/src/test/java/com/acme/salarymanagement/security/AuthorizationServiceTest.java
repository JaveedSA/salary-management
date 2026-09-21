package com.acme.salarymanagement.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class AuthorizationServiceTest {

    private final AuthorizationService authorizationService = new AuthorizationService();

    @Test
    void hrManagerHasFullCompensationAndImportPermissions() {
        Authentication authentication = authentication("HR_MANAGER");

        assertTrue(authorizationService.canManageEmployees(authentication));
        assertTrue(authorizationService.canManageCompensation(authentication));
        assertTrue(authorizationService.canReviewImports(authentication));
        assertTrue(authorizationService.canApplyImports(authentication));
        assertTrue(authorizationService.canViewReports(authentication));
        assertTrue(authorizationService.canReadAudit(authentication));
    }

    @Test
    void hrExecutiveCanReviewButCannotApplyImports() {
        Authentication authentication = authentication("HR_EXECUTIVE");

        assertTrue(authorizationService.canManageEmployees(authentication));
        assertTrue(authorizationService.canManageCompensation(authentication));
        assertTrue(authorizationService.canReviewImports(authentication));
        assertFalse(authorizationService.canApplyImports(authentication));
        assertTrue(authorizationService.canViewReports(authentication));
    }

    @Test
    void employeeCanReadOnlyOwnProfileAndCompensation() {
        Authentication authentication = authenticationWithEmployee("EMPLOYEE", 42L);

        assertTrue(authorizationService.canReadEmployee(authentication, 42L));
        assertFalse(authorizationService.canReadEmployee(authentication, 43L));
        assertTrue(authorizationService.canReadCompensation(authentication, 42L));
        assertFalse(authorizationService.canReadCompensation(authentication, 43L));
        assertFalse(authorizationService.canManageCompensation(authentication));
    }

    @Test
    void adminDoesNotReceiveImplicitSalaryAccess() {
        Authentication authentication = authentication("ADMIN");

        assertTrue(authorizationService.canAdminister(authentication));
        assertFalse(authorizationService.canReadCompensation(authentication, 42L));
        assertFalse(authorizationService.canViewReports(authentication));
        assertFalse(authorizationService.canReadAudit(authentication));
    }

    private static Authentication authentication(String role) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        doReturn(authorities).when(authentication).getAuthorities();
        return authentication;
    }

    private static Authentication authenticationWithEmployee(String role, Long employeeId) {
        Authentication authentication = authentication(role);
        AppUser user = mock(AppUser.class);
        when(user.getEmployeeId()).thenReturn(employeeId);
        when(authentication.getPrincipal()).thenReturn(user);
        return authentication;
    }
}
