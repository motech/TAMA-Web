package org.motechproject.tama.dailypillreminder.context;

import org.motechproject.tama.dailypillreminder.call.PillReminderCall;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import javax.servlet.http.HttpSession;

public class DailyPillReminderContext extends TAMAIVRContext {
    public static final String NUMBER_OF_TIMES_REMINDER_SENT = PillReminderCall.TIMES_SENT;
    private static final String TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER = PillReminderCall.TOTAL_TIMES_TO_SEND;
    private static final String RETRY_INTERVAL = PillReminderCall.RETRY_INTERVAL;
    private DailyPillReminderService dailyPillReminderService;

    protected DailyPillReminderContext() {
    }

    public DailyPillReminderContext(TAMAIVRContext tamaivrContext, DailyPillReminderService dailyPillReminderService) {
        super(tamaivrContext);
        this.dailyPillReminderService = dailyPillReminderService;
    }

    public PillRegimen pillRegimen() {
        HttpSession session = httpRequest.getSession();
        PillRegimen pillRegimen = (PillRegimen) session.getAttribute(PILL_REGIMEN);
        if (pillRegimen == null) {
            pillRegimen = dailyPillReminderService.getPillRegimen(patientId());
            session.setAttribute(PILL_REGIMEN, pillRegimen);
        }
        return pillRegimen;
    }

    public Dose previousDose() {
        return pillRegimen().getPreviousDoseAt(callStartTime());
    }

    public boolean isPreviousDoseTaken() {
        return previousDose() == null || previousDose().isTaken();
    }

    public Dose currentDose() {
        return pillRegimen().getDoseAt(callStartTime());
    }

    public Dose nextDose() {
        return pillRegimen().getNextDoseAt(callStartTime());
    }

    public boolean isCurrentDoseTaken() {
        return currentDose() == null || currentDose().isTaken();
    }

    public int numberOfTimesReminderSent() {
        return Integer.parseInt(kookooRequest.getParameter(NUMBER_OF_TIMES_REMINDER_SENT));
    }

    public int totalNumberOfTimesToSendReminder() {
        return Integer.parseInt(kookooRequest.getParameter(TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER));
    }

    public int retryInterval() {
        return Integer.parseInt(kookooRequest.getParameter(RETRY_INTERVAL));
    }
}
