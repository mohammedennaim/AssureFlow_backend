package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import com.pfe.notification.application.mapper.NotificationMapper;
import com.pfe.notification.domain.exception.NotificationNotFoundException;
import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.repository.NotificationRepository;
import com.pfe.notification.infrastructure.email.EmailNotificationService;
import com.pfe.notification.infrastructure.sms.TwilioSmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private TwilioSmsService twilioSmsService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationDto notificationDto;
    private CreateNotificationRequest request;
    private UUID notificationId;
    private UUID policyId;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        policyId = UUID.randomUUID();

        request = new CreateNotificationRequest();
        request.setRecipient("test@example.com");
        request.setContent("Test message");

        notification = Notification.builder()
                .id(notificationId)
                .policyId(policyId)
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .content("Test message")
                .status(NotificationStatus.PENDING)
                .build();

        notificationDto = new NotificationDto();
        notificationDto.setId(notificationId);
        notificationDto.setPolicyId(policyId);
        notificationDto.setRecipient("test@example.com");
        notificationDto.setContent("Test message");
        notificationDto.setStatus(NotificationStatus.PENDING);
    }

    @Nested
    @DisplayName("Create Notification Tests")
    class CreateNotificationTests {

        @Test
        @DisplayName("Should successfully create a notification")
        void shouldCreateNotificationSuccessfully() {
            when(notificationMapper.toDomain(request)).thenReturn(notification);
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
            when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

            NotificationDto result = notificationService.createNotification(request);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(NotificationStatus.PENDING);
            verify(notificationRepository).save(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("Get Notification Tests")
    class GetNotificationTests {

        @Test
        @DisplayName("Should get notification by ID successfully")
        void shouldGetNotificationByIdSuccessfully() {
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
            when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

            NotificationDto result = notificationService.getNotificationById(notificationId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(notificationId);
            verify(notificationRepository).findById(notificationId);
        }

        @Test
        @DisplayName("Should throw exception when notification not found")
        void shouldThrowExceptionWhenNotificationNotFound() {
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

            assertThrows(NotificationNotFoundException.class,
                    () -> notificationService.getNotificationById(notificationId));
        }
    }

    @Nested
    @DisplayName("Send Notification Tests")
    class SendNotificationTests {

        @Test
        @DisplayName("Should send notification successfully")
        void shouldSendNotificationSuccessfully() {
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
            doNothing().when(emailNotificationService).sendEmail(any(), any(), any());

            notificationService.sendNotificationInternal(notificationId);

            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
            verify(notificationRepository).save(notification);
        }
    }
}
