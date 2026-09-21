package com.acme.salarymanagement.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {

    private static final Set<Permission> HR_MANAGER = EnumSet.allOf(Permission.class);
    private static final Set<Permission> HR_EXECUTIVE = EnumSet.of(
            Permission.EMPLOYEE_READ,
            Permission.EMPLOYEE_WRITE,
            Permission.COMPENSATION_READ,
            Permission.COMPENSATION_WRITE,
            Permission.IMPORT_REVIEW,
            Permission.REPORT_VIEW);
    private static final Set<Permission> EMPLOYEE = EnumSet.of(
            Permission.EMPLOYEE_READ,
            Permission.COMPENSATION_READ);
    private static final Set<Permission> ADMIN = EnumSet.of(
            Permission.USER_ADMIN,
            Permission.CONFIG_ADMIN);

    private static final Map<UserRole, Set<Permission>> BY_ROLE = Map.of(
            UserRole.HR_MANAGER, HR_MANAGER,
            UserRole.HR_EXECUTIVE, HR_EXECUTIVE,
            UserRole.EMPLOYEE, EMPLOYEE,
            UserRole.ADMIN, ADMIN);

    private RolePermissions() {
    }

    public static boolean allows(UserRole role, Permission permission) {
        return BY_ROLE.getOrDefault(role, Set.of()).contains(permission);
    }

    public static Set<Permission> forRole(UserRole role) {
        return BY_ROLE.getOrDefault(role, Set.of());
    }
}