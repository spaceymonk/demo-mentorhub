package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
@EnableScheduling
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final MentorshipRepository mentorshipRepository;

    public EmailService(MentorshipRepository mentorshipRepository) {
        this.mentorshipRepository = mentorshipRepository;
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);
        mailSender.setUsername("");
        mailSender.setPassword("");
    }


    public void send(String from, String to, String subject, String text) {

        try {
            var mimeMessage = mailSender.createMimeMessage();
            var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void send(String to, String subject, String text) {
        send("system@mentorhub.com", to, subject, text);
    }

    //    @Scheduled(cron = "0 */15 * ? * *")  -- every 15 minutes
    @Scheduled(cron = "0 * * ? * *") // every minute
    public void run() {
        // fetch all 1 hour remained mentorships
        //todo
    }

}
