package com.spaceymonk.mentorhub.features;

import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Email service class for scheduled operations.
 * <p>
 * You can set new scheduled event that uses email service by simply adding
 * a new method and annotate it with <code>@Scheduled</code>.
 *
 * @author spaceymonk
 * @version v1.0 08/18/21
 * @see EmailSender
 */
@Service
@EnableScheduling
public class ScheduledEmailService {

    private EmailSender emailSender;
    private MentorshipRepository mentorshipRepository;

    /**
     * Sets email sender.
     *
     * @param emailSender the email sender
     */
    @Autowired
    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Sets mentorship repository.
     *
     * @param mentorshipRepository the mentorship repository
     */
    @Autowired
    public void setMentorshipRepository(MentorshipRepository mentorshipRepository) {
        this.mentorshipRepository = mentorshipRepository;
    }

    /**
     * Send email to mentor and mentees when phase ending.
     * <p>
     * This function will be called every minute and does below operations:
     * <ol>
     *     <li>Fetch mentorships which phases will end 1 hour from the call time from database.</li>
     *     <li>Generate a reminder email text.</li>
     *     <li>Send mail to both mentor and mentee.</li>
     * </ol>
     */
    @Scheduled(cron = "0 * * ? * *") // every minute
    public void runEmailReminder() {
        final long ONE_HOUR_IN_MS = 60 * 60 * 1000;
        var now = new Date();
        var oneHourLater = new Date(now.getTime() + ONE_HOUR_IN_MS);
        var list = mentorshipRepository.findByPhasesEndDateBetweenAndPhasesNotifiedAndCurrentPhaseIndexGreaterThan(now, oneHourLater, false, -1);
        list.forEach(mentorship -> {
            mentorship.getPhases().forEach(phase -> {
                if (phase.getEndDate().after(now) && phase.getEndDate().before(oneHourLater) && !phase.getNotified()) {
                    String message = "<p>Hey don't forget the phase of <strong>" + phase.getName() + "</strong> about <strong>" + mentorship.getMajorSubject() + "</strong>!<p>";
                    emailSender.send(mentorship.getMentor().getEmail(), "MentorHub Phase Reminder", message);
                    emailSender.send(mentorship.getMentee().getEmail(), "MentorHub Phase Reminder", message);
                    phase.setNotified(true);
                }
            });
            mentorshipRepository.save(mentorship);
        });
    }
}
