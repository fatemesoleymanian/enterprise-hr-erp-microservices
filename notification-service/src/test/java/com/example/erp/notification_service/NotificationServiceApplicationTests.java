package com.example.erp.notification_service;

import com.example.erp.notification_service.repository.NotificationRepository;
import com.example.erp.notification_service.repository.ProcessedNotificationEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class NotificationServiceApplicationTests {

	@MockitoBean
	private NotificationRepository notificationRepository;

	@MockitoBean
	private ProcessedNotificationEventRepository processedEventRepository;

	@Test
	void contextLoads() {
	}

}
