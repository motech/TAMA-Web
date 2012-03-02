package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.LocalDate;
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

    public boolean shouldRaiseAlert(ClinicVisit clinicVisit) {
        if (clinicVisit.getConfirmedAppointmentDate() != null) {
            return false;
        } else {
            LocalDate referenceDate = clinicVisit.getEffectiveDueDate();
            return DateUtil.today().equals(referenceDate.minusDays(DAYS_FOR_REMINDER_ALERT));
        }
    }
}
