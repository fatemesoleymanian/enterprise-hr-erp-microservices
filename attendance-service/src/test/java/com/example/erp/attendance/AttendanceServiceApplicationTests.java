package com.example.erp.attendance;

import com.example.erp.attendance.event.AttendanceEventPublisher;
import com.example.erp.attendance.repository.AttendanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AttendanceServiceApplicationTests {

	@MockitoBean
	private AttendanceRecordRepository attendanceRecordRepository;

	@MockitoBean
	private AttendanceEventPublisher attendanceEventPublisher;

	@Test
	void contextLoads() {
	}

}
