package com.dao.momentum.email.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.email.exception.EmailFailException;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    public void sendPasswordResetEmail(Employee employee, String token) {
        // 비밀번호 재설정 링크
        String resetLink = "http://localhost:5173/password/reset?token=" + token;

        // 이메일 제목
        String subject = "Momentum 비밀번호 재설정";

        // 받는 사람 이메일
        String to = employee.getEmail();

        // 이메일 전체 구조 정의하는 틀
        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String htmlMsg = templateEngine.process("email/reset-password", context);
        try {
            sendEmail(to, subject, htmlMsg);
        }catch(MessagingException e){
            throw new EmailFailException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }


    public void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        // html 내용 전송을 도와주는 객체
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("이메일을 {}로 전송했습니다. 제목: {}", to, subject);
    }
}
