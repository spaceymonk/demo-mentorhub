package com.spaceymonk.mentorhub.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;


/**
 * Email customizer class for email services.
 * <p>
 * All emails are Mime type of <code>HTML</code>.
 *
 * @author spaceymonk
 * @version v1.0 08/18/21
 */
@Component
public class EmailSender {

    public static final String SYSTEM_MAIL_ADDRESS = "system@mentorhub.com";
    private final JavaMailSenderImpl mailSender;

    /**
     * Instantiates a new Email service.
     * Do not instantiate this class by yourself, use Spring dependency injection to get an instance.
     */
    @Autowired
    public EmailSender(@Value("${SMTP_HOST:localhost}") String HOST,
                       @Value("${SMTP_PORT:1025}") String PORT) {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(Integer.parseInt(PORT));
        mailSender.setUsername("");
        mailSender.setPassword("");
    }

    /**
     * Send an email.
     *
     * @param from    the from
     * @param to      the to
     * @param subject the subject
     * @param text    the text
     */
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

    /**
     * Send an email with system mail address.
     *
     * @param to      the to
     * @param subject the subject
     * @param text    the text
     */
    public void send(String to, String subject, String text) {
        send(SYSTEM_MAIL_ADDRESS, to, subject, text);
    }
}
