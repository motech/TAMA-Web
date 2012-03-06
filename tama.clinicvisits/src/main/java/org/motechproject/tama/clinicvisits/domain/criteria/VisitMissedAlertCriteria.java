package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class VisitMissedAlertCriteria {

    public boolean shouldRaiseAlert(ClinicVisit clinicVisit) {
        final boolean notVisited = clinicVisit.getVisitDate() == null;
        return notVisited && DateUtil.today().isAfter(clinicVisit.getConfirmedAppointmentDate().toLocalDate());
    }
}
