package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ReminderAlertCriteria {

    private final int DAYS_FOR_REMINDER_ALERT;

    @Autowired
    public ReminderAlertCriteria(@Qualifier("appointments") Properties appointmentProperties) {
        String dayForReminderAlertProperty = appointmentProperties.getProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED);
        this.DAYS_FOR_REMINDER_ALERT = Integer.parseInt(dayForReminderAlertProperty);
    }

    public boolean shouldRaiseAlert(Appointment appointment) {
        if (appointment.firmDate() != null) {
            return false;
        } else {
            LocalDate referenceDate = effectiveDueDate(appointment);
            return DateUtil.today().equals(referenceDate.minusDays(DAYS_FOR_REMINDER_ALERT));
        }
    }

    private LocalDate effectiveDueDate(Appointment appointment) {
        if (appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE) != null) {
            return new LocalDate(appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE));
        } else {
            return appointment.dueDate().toLocalDate();
        }
    }
}
