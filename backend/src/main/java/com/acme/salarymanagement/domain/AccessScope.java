package com.acme.salarymanagement.domain;

public record AccessScope(String country, String department, String businessUnit) {

    public boolean includes(String employeeCountry, String employeeDepartment, String employeeBusinessUnit) {
        return matches(country, employeeCountry)
                && matches(department, employeeDepartment)
                && matches(businessUnit, employeeBusinessUnit);
    }

    private static boolean matches(String configuredValue, String actualValue) {
        return configuredValue == null || configuredValue.equals(actualValue);
    }
}