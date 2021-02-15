package io.github.tpalucki.grammati.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final Parser markdownParser;
    private final HtmlRenderer markdownHtmlRenderer;

    void sendSubscriptionConfirmation(String email, String name, String confirmUrl) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom("noreply@grammati.com");
            helper.setSubject("Confirm your subscription for Gramilka");
            helper.setTo(email);
            helper.setValidateAddresses(true);

            String htmlMessage = provideHtmlMessage(name, confirmUrl);
            helper.setText(htmlMessage, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            log.error("Cannot sent subscription confirmation email");
        }
    }

    private String provideHtmlMessage(String name, String confirmUrl) throws IOException {
        String tempalate = this.getClass().getClassLoader().getResource("./templates/subscriptionConfirmation.md").getFile();

        String templateContent = Files.readString(Paths.get(tempalate));
        templateContent = templateContent.replace("{{name}}", name).replace("{{confirmationLink}}", confirmUrl);

        Node document = markdownParser.parse(templateContent);
        return markdownHtmlRenderer.render(document);
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

    public void sendSimpleMail() throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom("tomasz@gramilka.com");
        helper.setSubject("Welcome");
        helper.setTo("tpalucki@protonmail.com");
        helper.setBcc("tpalucki@gmail.com");
        helper.setValidateAddresses(true);
        helper.setText("Jak się masz? to jest pierwszy email.\nTomek");

        mailSender.send(mimeMessage);
    }
}
