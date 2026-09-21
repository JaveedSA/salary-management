package com.acme.salarymanagement.reporting;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/compensation")
public class CompensationReportController {

    private final CompensationReportService service;

    public CompensationReportController(CompensationReportService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompensationReportEntry> filter(@ModelAttribute CompensationReportFilter filter) {
        return service.filter(filter);
    }

    @GetMapping("/metrics")
    public List<CompensationMetric> metrics(@ModelAttribute CompensationReportFilter filter) {
        return service.metrics(filter);
    }
}