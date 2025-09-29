package com.tejusko.user_service.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	 @Mock
	    private JavaMailSender mailSender;

	    @InjectMocks
	    private EmailService emailService;

	    @Test
	    void testSendEmail() {
	        String to = "test@example.com";
	        String subject = "Test Subject";
	        String body = "Hello, this is a test email";

	        // Call the method
	        emailService.sendEmail(to, subject, body);

	        // Verify that JavaMailSender.send() was called with a SimpleMailMessage
	        verify(mailSender, times(1)).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));
	    }
}
