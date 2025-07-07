package com.dao.momentum.email.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.email.exception.EmailFailException;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private MimeMessage mimeMessage;
    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_shouldSendSuccessfully() throws MessagingException {
        // given
        Employee employee = mock(Employee.class);
        when(employee.getEmail()).thenReturn("test@example.com");

        String token = "dummy-token";
        String expectedHtml = "<html>Email content</html>";

        when(templateEngine.process(eq("email/reset-password"), any(Context.class)))
                .thenReturn(expectedHtml);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // MimeMessageHelper는 실제 객체 사용 (생성자 테스트)
        doAnswer(invocation -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo("test@example.com");
            helper.setSubject("Momentum 비밀번호 재설정");
            helper.setText(expectedHtml, true);
            return null;
        }).when(mailSender).send(mimeMessage);

        // when
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(employee, token));

        // then
        verify(templateEngine).process(eq("email/reset-password"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_shouldThrowEmailFailException_whenMessagingExceptionOccurs() throws MessagingException {
        // given
        Employee employee = mock(Employee.class);
        when(employee.getEmail()).thenReturn("fail@example.com");

        String token = "fail-token";
        when(templateEngine.process(any(String.class),any(Context.class))).thenReturn("HTML");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException(new MessagingException()))
                .when(mailSender).send(any(MimeMessage.class));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetEmail(employee, token);
        });

        // 감싸고 있는 원인이 MessagingException인지 확인
        assertInstanceOf(MessagingException.class, exception.getCause());
    }


}