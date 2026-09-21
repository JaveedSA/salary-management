package com.acme.salarymanagement.security;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    public boolean canManageEmployees(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER") || hasRole(authentication, "HR_EXECUTIVE");
    }

    public boolean canReadEmployee(Authentication authentication, Long employeeId) {
        return canManageEmployees(authentication) || isSelf(authentication, employeeId);
    }

    public boolean canReadCompensation(Authentication authentication, Long employeeId) {
        return hasSalaryRole(authentication) && (canManageEmployees(authentication) || isSelf(authentication, employeeId));
    }

    public boolean canManageCompensation(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER") || hasRole(authentication, "HR_EXECUTIVE");
    }

    public boolean canReviewImports(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER") || hasRole(authentication, "HR_EXECUTIVE");
    }

    public boolean canApplyImports(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER");
    }

    public boolean canViewReports(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER") || hasRole(authentication, "HR_EXECUTIVE");
    }

    public boolean canReadAudit(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER");
    }

    public boolean canAdminister(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    public void require(boolean allowed) {
        if (!allowed) {
            throw new AccessDeniedException("Operation is not permitted");
        }
    }

    private boolean hasSalaryRole(Authentication authentication) {
        return hasRole(authentication, "HR_MANAGER")
                || hasRole(authentication, "HR_EXECUTIVE")
                || hasRole(authentication, "EMPLOYEE");
    }

    private boolean isSelf(Authentication authentication, Long employeeId) {
        if (employeeId == null || !(authentication.getPrincipal() instanceof AppUser user)) {
            return false;
        }
        return employeeId.equals(user.getEmployeeId());
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(authority -> ("ROLE_" + role).equals(authority.getAuthority()));
    }
}