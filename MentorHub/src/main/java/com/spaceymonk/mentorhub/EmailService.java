package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Date;

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
        final long ONE_HOUR_IN_MS = 60 * 60 * 1000;
        var now = new Date();
        var oneHourLater = new Date(now.getTime() + ONE_HOUR_IN_MS);
        System.out.println(now);
        System.out.println(oneHourLater);
        var list = mentorshipRepository.findByPhasesEndDateBetweenAndPhasesNotifiedFalse(now, oneHourLater);
        list.forEach(mentorship -> {
            mentorship.getPhases().forEach(phase -> {
                if (phase.getEndDate().after(now) && phase.getEndDate().after(oneHourLater)) {
                    send(mentorship.getMentor().getEmail(), "MentorHub Phase Reminder",
                            "<p>Hey don't forget the phase of <strong>" + phase.getName() + "</strong> about <strong>" + mentorship.getMajorSubject() + "</strong>!<p>");
                    phase.setNotified(true);
                }
            });
            mentorshipRepository.save(mentorship);
        });
    }

}
