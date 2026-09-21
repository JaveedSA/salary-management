package com.acme.salarymanagement.employee;

import java.time.LocalDate;

import com.acme.salarymanagement.domain.EmployeeProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_identifier", nullable = false, unique = true)
    private String employeeIdentifier;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "work_email", nullable = false, unique = true)
    private String workEmail;
    @Column(name = "employment_status", nullable = false)
    private String employmentStatus;
    @Column(name = "employment_type", nullable = false)
    private String employmentType;
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    @Column(name = "termination_date")
    private LocalDate terminationDate;
    @Column(nullable = false)
    private String country;
    private String location;
    @Column(name = "legal_entity")
    private String legalEntity;
    private String department;
    @Column(name = "business_unit")
    private String businessUnit;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "job_family")
    private String jobFamily;
    @Column(name = "job_level")
    private String jobLevel;
    @Column(name = "manager_identifier")
    private String managerIdentifier;
    @Column(name = "cost_center")
    private String costCenter;

    protected EmployeeEntity() {
    }

    public EmployeeEntity(EmployeeProfile profile) {
        update(profile);
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeIdentifier() {
        return employeeIdentifier;
    }

    public EmployeeProfile toProfile() {
        return new EmployeeProfile(employeeIdentifier, fullName, workEmail, employmentStatus, employmentType,
                hireDate, terminationDate, country, location, legalEntity, department, businessUnit, jobTitle,
            jobFamily, jobLevel, managerIdentifier, costCenter, id);
    }

    public void update(EmployeeProfile profile) {
        employeeIdentifier = profile.employeeIdentifier();
        fullName = profile.fullName();
        workEmail = profile.workEmail();
        employmentStatus = profile.employmentStatus();
        employmentType = profile.employmentType();
        hireDate = profile.hireDate();
        terminationDate = profile.terminationDate();
        country = profile.country();
        location = profile.location();
        legalEntity = profile.legalEntity();
        department = profile.department();
        businessUnit = profile.businessUnit();
        jobTitle = profile.jobTitle();
        jobFamily = profile.jobFamily();
        jobLevel = profile.jobLevel();
        managerIdentifier = profile.managerIdentifier();
        costCenter = profile.costCenter();
    }
}