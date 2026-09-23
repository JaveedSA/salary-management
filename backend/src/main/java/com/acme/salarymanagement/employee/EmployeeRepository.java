package com.acme.salarymanagement.employee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmployeeIdentifier(String employeeIdentifier);

    List<EmployeeEntity> findByEmployeeIdentifierIn(Collection<String> employeeIdentifiers);

    default Map<String, EmployeeEntity> findMapByEmployeeIdentifierIn(Collection<String> employeeIdentifiers) {
        List<String> identifiers = new ArrayList<>(employeeIdentifiers);
        List<EmployeeEntity> employees = new ArrayList<>();
        for (int start = 0; start < identifiers.size(); start += 500) {
            int end = Math.min(start + 500, identifiers.size());
            employees.addAll(findByEmployeeIdentifierIn(identifiers.subList(start, end)));
        }
        return employees.stream()
                .collect(Collectors.toMap(EmployeeEntity::getEmployeeIdentifier, employee -> employee));
    }

    boolean existsByEmployeeIdentifierAndIdNot(String employeeIdentifier, long id);

    List<EmployeeEntity> findByFullNameContainingIgnoreCaseOrEmployeeIdentifierContainingIgnoreCase(
            String name, String employeeIdentifier);
}