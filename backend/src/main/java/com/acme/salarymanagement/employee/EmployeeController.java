package com.acme.salarymanagement.employee;

import java.util.List;

import com.acme.salarymanagement.domain.EmployeeProfile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public EmployeeProfile get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<EmployeeProfile> search(@RequestParam(defaultValue = "") String query) {
        return service.search(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeProfile create(@RequestBody EmployeeProfile profile) {
        return service.create(profile);
    }

    @PutMapping("/{id}")
    public EmployeeProfile update(@PathVariable long id, @RequestBody EmployeeProfile profile) {
        return service.update(id, profile);
    }
}