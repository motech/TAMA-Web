package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RaiseAppointmentConfirmationCriteria {

    private final int DAYS_FOR_REMINDER_ALERT;

    @Autowired
    public RaiseAppointmentConfirmationCriteria(@Qualifier("appointments") Properties appointmentProperties) {
        String dayForReminderAlertProperty = appointmentProperties.getProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED);
        this.DAYS_FOR_REMINDER_ALERT = Integer.parseInt(dayForReminderAlertProperty);
    }

    public boolean shouldRaiseAlert(Appointment appointment) {
        if (isAppointmentConfirmed(appointment)) {
            return false;
        } else {
            LocalDate referenceDate = referenceDate(appointment);
            return DateUtil.today().equals(referenceDate.minusDays(DAYS_FOR_REMINDER_ALERT));
        }
    }

    private boolean isAppointmentConfirmed(Appointment appointment) {
        return appointment.firmDate() != null;
    }

    private LocalDate referenceDate(Appointment appointment) {
        if (null != appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE)) {
            return new LocalDate((String) appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE));
        } else {
            return appointment.dueDate().toLocalDate();
        }
    }
}
