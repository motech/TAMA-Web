package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class TAMAReminderConfiguration {

    public static final String REMIND_FROM = "remindFrom";
    public static final String PUSHED_APPOINTMENT_REMINDER_MESSAGE_COUNT = "pushedAppointmentReminderMessageCount";
    private static final String PUSHED_VISIT_REMINDER_MESSAGE_COUNT = "pushedVisitReminderMessageCount";
    public static final String REMIND_FOR_VISIT_FROM = "remindForVisitFrom";

    private Properties appointmentsProperties;
    private Integer pushedAppointmentReminderMessageCount;
    private int pushedVisitReminderMessageCount;

    @Autowired
    public TAMAReminderConfiguration(@Qualifier("appointments") Properties appointmentsProperties,
                                     @Value("#{appointments['" + PUSHED_APPOINTMENT_REMINDER_MESSAGE_COUNT + "']}") Integer pushedAppointmentReminderMessageCount,
                                     @Value("#{appointments['" + PUSHED_VISIT_REMINDER_MESSAGE_COUNT + "']}") Integer pushedVisitReminderMessageCount) {
        this.appointmentsProperties = appointmentsProperties;
        this.pushedAppointmentReminderMessageCount = pushedAppointmentReminderMessageCount;
        this.pushedVisitReminderMessageCount = pushedVisitReminderMessageCount;
    }

    public DateTime reminderStartDate(ClinicVisit clinicVisit) {
        DateTime appointmentDueDate = clinicVisit.getAppointmentDueDate();
        int remindFrom = Integer.parseInt(appointmentsProperties.getProperty(REMIND_FROM));

        return (null != appointmentDueDate) ? appointmentDueDate.minusDays(remindFrom) : null;
    }


    public DateTime visitReminderStartDate(ClinicVisit clinicVisit) {
        DateTime confirmedDate = clinicVisit.getConfirmedAppointmentDate();
        int remindFrom = Integer.parseInt(appointmentsProperties.getProperty(REMIND_FOR_VISIT_FROM));

        return (null != confirmedDate) ? confirmedDate.minusDays(remindFrom) : null;
    }

    public int getRemindAppointmentsFrom() {
        return Integer.parseInt(appointmentsProperties.getProperty(REMIND_FROM));
    }

    public int getVisitReminderFrom() {
        return Integer.parseInt(appointmentsProperties.getProperty(REMIND_FOR_VISIT_FROM));
    }

    public int getPushedAppointmentReminderVoiceMessageCount() {
        return pushedAppointmentReminderMessageCount;
    }

    public int getPushedVisitReminderVoiceMessageCount() {
        return pushedVisitReminderMessageCount;
    }
}
