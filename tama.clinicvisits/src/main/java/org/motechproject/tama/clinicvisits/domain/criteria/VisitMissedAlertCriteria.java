package org.motechproject.tama.clinicvisits.domain.criteria;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class VisitMissedAlertCriteria {

    public boolean shouldRaiseAlert(ClinicVisit clinicVisit) {
        final boolean notVisited = clinicVisit.getVisitDate() == null;
        return notVisited && DateUtil.today().isAfter(clinicVisit.getConfirmedAppointmentDate().toLocalDate());
    }
}
