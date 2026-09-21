package com.acme.salarymanagement.imports;

import com.acme.salarymanagement.security.AppUser;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final ImportService service;

    public ImportController(ImportService service) {
        this.service = service;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ImportBatchSummary stage(@RequestPart("file") MultipartFile file, Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        return service.stage(file, user.getId());
    }

    @PostMapping("/{batchId}/validate")
    public ImportBatchSummary validate(@PathVariable long batchId) {
        return service.validate(batchId);
    }

    @GetMapping("/{batchId}")
    public ImportBatchDetails details(@PathVariable long batchId) {
        return service.details(batchId);
    }

    @GetMapping(value = "/{batchId}/rejected", produces = "text/csv")
    public ResponseEntity<String> rejectedRows(@PathVariable long batchId) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv"))
                .body(service.rejectedRowsCsv(batchId));
    }

    @PostMapping("/{batchId}/apply")
    public ImportBatchSummary apply(@PathVariable long batchId,
            @org.springframework.web.bind.annotation.RequestParam boolean confirm) {
        return service.apply(batchId, confirm);
    }
}
