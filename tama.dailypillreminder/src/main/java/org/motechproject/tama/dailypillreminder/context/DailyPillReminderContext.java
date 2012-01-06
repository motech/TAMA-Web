package org.motechproject.tama.dailypillreminder.context;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.call.PillReminderCall;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import javax.servlet.http.HttpSession;

public class DailyPillReminderContext extends TAMAIVRContext {
    public static final String NUMBER_OF_TIMES_REMINDER_SENT = PillReminderCall.TIMES_SENT;
    private static final String TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER = PillReminderCall.TOTAL_TIMES_TO_SEND;
    private static final String RETRY_INTERVAL = PillReminderCall.RETRY_INTERVAL;

    protected DailyPillReminderContext() {
    }

    public DailyPillReminderContext(TAMAIVRContext tamaivrContext) {
        super(tamaivrContext);
    }

    public PillRegimen pillRegimen(PillReminderService pillReminderService) {
        return new PillRegimen(pillRegimenResponse(pillReminderService));
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

    private PillRegimenResponse pillRegimenResponse(PillReminderService pillReminderService) {
        HttpSession session = httpRequest.getSession();
        PillRegimenResponse pillRegimen = (PillRegimenResponse) session.getAttribute(PILL_REGIMEN);
        if (pillRegimen == null) {
            pillRegimen = pillReminderService.getPillRegimen(patientId());
            session.setAttribute(PILL_REGIMEN, pillRegimen);
        }
        return pillRegimen;
    }
}
