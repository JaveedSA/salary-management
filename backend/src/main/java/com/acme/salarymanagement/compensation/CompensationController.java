package com.acme.salarymanagement.compensation;

import java.util.List;

import com.acme.salarymanagement.domain.CompensationRecord;
import com.acme.salarymanagement.domain.CompensationTimelineItem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compensation")
public class CompensationController {

    private final CompensationService service;

    public CompensationController(CompensationService service) {
        this.service = service;
    }

    @GetMapping("/employee/{employeeId}")
    public List<CompensationTimelineItem> history(@PathVariable long employeeId) {
        return service.history(employeeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompensationRecord create(@RequestBody CompensationRecord compensationRecord) {
        return service.create(compensationRecord);
    }
}