package org.motechproject.tama.clinicvisits.domain.criteria;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class AppointmentConfirmationMissedAlertCriteria {

    public boolean shouldRaiseAlert(ClinicVisit clinicVisit) {
        final boolean appointmentNotConfirmed = clinicVisit.getConfirmedAppointmentDate() == null;
        return appointmentNotConfirmed && DateUtil.today().isAfter(clinicVisit.getEffectiveDueDate());
    }
}
