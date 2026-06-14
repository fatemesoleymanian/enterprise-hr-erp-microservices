package com.example.erp.reporting_service.repository;

import com.example.erp.reporting_service.projection.ProcessedReportingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedReportingEventRepository extends JpaRepository<ProcessedReportingEvent, UUID> {
}
