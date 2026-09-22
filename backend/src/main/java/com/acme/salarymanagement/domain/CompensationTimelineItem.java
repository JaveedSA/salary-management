package com.acme.salarymanagement.domain;

public record CompensationTimelineItem(long compensationRecordId, CompensationRecord compensation,
	CompensationPeriod period) {
}