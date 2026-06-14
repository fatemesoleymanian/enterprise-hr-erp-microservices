package com.example.erp.reporting_service;

import com.example.erp.reporting_service.repository.AttendanceMonthlyReportViewRepository;
import com.example.erp.reporting_service.repository.AttendanceReportContributionRepository;
import com.example.erp.reporting_service.repository.DepartmentReportViewRepository;
import com.example.erp.reporting_service.repository.EmployeeReportViewRepository;
import com.example.erp.reporting_service.repository.ProcessedReportingEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ReportingServiceApplicationTests {

	@MockitoBean
	private EmployeeReportViewRepository employeeRepository;

	@MockitoBean
	private DepartmentReportViewRepository departmentRepository;

	@MockitoBean
	private AttendanceMonthlyReportViewRepository monthlyAttendanceRepository;

	@MockitoBean
	private AttendanceReportContributionRepository contributionRepository;

	@MockitoBean
	private ProcessedReportingEventRepository processedEventRepository;

	@Test
	void contextLoads() {
	}

}
