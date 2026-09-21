package com.acme.salarymanagement.imports;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportFailureService {

    private final ImportBatchRepository repository;

    public ImportFailureService(ImportBatchRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(long batchId) {
        repository.findById(batchId).ifPresent(batch -> {
            batch.markFailed();
            repository.save(batch);
        });
    }
}