package com.acme.salarymanagement.compensation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import com.acme.salarymanagement.domain.ApprovalStatus;
import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationType;
import com.acme.salarymanagement.domain.EmployeeProfile;
import com.acme.salarymanagement.audit.AuditEventEntity;
import com.acme.salarymanagement.audit.AuditEventRepository;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:file:salary-query-test?mode=memory&cache=shared",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class CompensationRepositoryQueryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

        @Autowired
        private AuditEventRepository auditEventRepository;

    @Test
    void selectsEmployeeHistoryInEffectiveDateOrderAndFindsOverlaps() {
        EmployeeEntity employee = employeeRepository.save(new EmployeeEntity(new EmployeeProfile(
                "EMP-QUERY", "Query Employee", "query@example.com", "ACTIVE", "FULL_TIME",
                LocalDate.of(2020, 1, 1), null, "GB", "London", "ACME", "Engineering", "Technology",
                "Engineer", "Engineering", "P2", null, "CC-1", null)));
        compensationRepository.save(new CompensationEntity(new CompensationRecord(employee.getId(),
                CompensationType.BASE_SALARY, 100_000, "GBP", "MONTHLY", LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31), "prior", ApprovalStatus.APPROVED)));
        compensationRepository.save(new CompensationEntity(new CompensationRecord(employee.getId(),
                CompensationType.BASE_SALARY, 110_000, "GBP", "MONTHLY", LocalDate.of(2026, 1, 1),
                null, "current", ApprovalStatus.PENDING)));

        var history = compensationRepository.findByEmployeeIdOrderByEffectiveFromDesc(employee.getId());
        var overlap = compensationRepository.findOverlapping(employee.getId(), CompensationType.BASE_SALARY,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 7, 1));

        assertEquals(2, history.size());
        assertEquals(110_000, history.get(0).getAmountMinorUnits());
        assertEquals(1, overlap.size());
    }

        @Test
        void supportsIdentifierLookupAndAuditHistoryAtTargetPopulation() {
                List<EmployeeEntity> employees = new ArrayList<>();
                for (int index = 0; index < 10_000; index++) {
                        String identifier = "EMP-SCALE-" + index;
                        employees.add(new EmployeeEntity(new EmployeeProfile(
                                        identifier, "Scale Employee " + index, identifier.toLowerCase() + "@example.com",
                                        "ACTIVE", "FULL_TIME", LocalDate.of(2020, 1, 1), null, "US", "New York",
                                            "ACME", "Engineering", "Technology", "Engineer", "Engineering",
                                        "P2", null, "CC-1", null)));
                }
                employeeRepository.saveAll(employees);

                EmployeeEntity target = employeeRepository.findByEmployeeIdentifier("EMP-SCALE-9999").orElseThrow();
                auditEventRepository.save(new AuditEventEntity(null, "EMPLOYEE", target.getId(), "CREATED",
                                null, "created", null, "TEST", null, null));
                auditEventRepository.save(new AuditEventEntity(null, "EMPLOYEE", target.getId(), "UPDATED",
                                "created", "updated", "test", "TEST", null, null));

                var auditHistory = auditEventRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(
                                "EMPLOYEE", target.getId());

                assertEquals("EMP-SCALE-9999", target.getEmployeeIdentifier());
                assertEquals(2, auditHistory.size());
                assertEquals("UPDATED", auditHistory.get(1).getAction());
                assertTrue(auditHistory.get(0).getCreatedAt().compareTo(auditHistory.get(1).getCreatedAt()) <= 0);
        }
}
