package com.jobflow.service;

import com.jobflow.entity.JobApplication;
import com.jobflow.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class NotificationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot-token:}")
    private String telegramBotToken;

    @Value("${spring.mail.from:noreply@jobflow.dev}")
    private String mailFrom;

    // Every day at 08:00
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDeadlineReminders() {
        LocalDate limit = LocalDate.now().plusDays(3);
        List<JobApplication> upcoming = jobApplicationRepository.findUpcomingDeadlines(limit);
        for (JobApplication app : upcoming) {
            String email = app.getUser().getEmail();
            if (email == null)
                continue;
            String subject = "JobFlow: Deadline approaching — " + app.getRole() + " at " + app.getCompany();
            String body = """
                    Hi,

                    Your application for %s at %s has a deadline on %s.

                    Don't forget to submit!

                    — JobFlow AI
                    """.formatted(app.getRole(), app.getCompany(), app.getDeadline());
            sendEmail(email, subject, body);
        }
        log.info("Deadline reminders sent: {}", upcoming.size());
    }

    // Every day at 09:00
    @Scheduled(cron = "0 0 9 * * *")
    public void sendFollowUpReminders() {
        Instant since = Instant.now().minus(2, ChronoUnit.DAYS);
        List<JobApplication> awaiting = jobApplicationRepository.findAllAwaitingResponse(since);
        for (JobApplication app : awaiting) {
            String email = app.getUser().getEmail();
            String subject = "JobFlow: Time to follow up — " + app.getRole() + " at " + app.getCompany();
            String body = """
                    Hi,

                    It has been over 2 days since you applied for %s at %s with no response.

                    Consider sending a follow-up email to show your continued interest.

                    — JobFlow AI
                    """.formatted(app.getRole(), app.getCompany());

            if (email != null) {
                sendEmail(email, subject, body);
            }

            String chatId = app.getUser().getTelegramChatId();
            if (chatId != null && !telegramBotToken.isBlank()) {
                String message = "⏰ Follow-up reminder: *%s* at *%s* — no response in 2+ days."
                        .formatted(app.getRole(), app.getCompany());
                sendTelegram(chatId, message);
            }
        }
        log.info("Follow-up reminders sent: {}", awaiting.size());
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailFrom);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private void sendTelegram(String chatId, String text) {
        try {
            String url = "https://api.telegram.org/bot%s/sendMessage".formatted(telegramBotToken);
            restTemplate.postForObject(url, Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "Markdown"), Map.class);
        } catch (Exception e) {
            log.warn("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
        }
    }
}
