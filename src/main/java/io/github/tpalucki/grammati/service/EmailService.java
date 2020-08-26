package io.github.tpalucki.grammati.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@grammati.com");
        message.setTo(email);
        message.setSubject("Grammati: Please confirm your subscription");
        message.setText("Thanks for subscribing to Grammati. From now you will be receiving daily english exercises. To confirm this subscription please click below:\nConfirm\nGrammati Team");

        mailSender.send(message);
    }

    public boolean isOn() {
        return false;
    }

    public void sendDailyQuiz(String to, String quizId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("daily-quiz@grammati.com");
        message.setTo(to);
        message.setSubject("Your daily quiz");
        message.setText("Your daily quiz http://localhost:8080/exercises/" + quizId);

        mailSender.send(message);
    }
}
