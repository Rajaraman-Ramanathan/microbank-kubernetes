package com.microbank.notification.service.impl;

import com.microbank.notification.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${sender-email}")
    private String senderEmail;

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendActivationMail(String to, String firstName, String lastName, String activationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("lastName", lastName);
        context.setVariable("activationCode", activationCode);

        String content = templateEngine.process("activation-email", context);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("Account Activation Code");
        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendTransactionMail(
            String to,
            UUID transactionId,
            String senderName,
            String receiverName,
            String senderIban,
            String receiverIban,
            BigDecimal amount,
            String description,
            LocalDateTime timestamp
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("transactionId", transactionId.toString());
        context.setVariable("senderName", senderName);
        context.setVariable("receiverName", receiverName);
        context.setVariable("senderIban", senderIban);
        context.setVariable("receiverIban", receiverIban);
        context.setVariable("amount", amount.toPlainString());
        context.setVariable("description", description);
        context.setVariable("timestamp", timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String content = templateEngine.process("transaction-email", context);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("Transaction Notification");
        helper.setText(content, true);
        log.info("Sending transaction email to {}", to);
        mailSender.send(message);
        log.info("Transaction email sent successfully to {}", to);
    }

    @Override
    public void sendPasswordRecoveryMail(String to, String passwordRecoveryCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("passwordRecoveryCode", passwordRecoveryCode);

        String content = templateEngine.process("password-recovery-email", context);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("Password Recovery Code");
        helper.setText(content, true);

        mailSender.send(message);
    }

}
