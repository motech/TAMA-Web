package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Collections;


public class ClinicVisits extends ArrayList<ClinicVisit> {

    public DateTime nextAppointmentDueDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        return allPendingVisits.hasVisits() ? allPendingVisits.get(0).getAppointmentDueDate() : null;
    }

    public DateTime nextConfirmedAppointmentDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        return allPendingVisits.hasVisits() ? allPendingVisits.get(0).getConfirmedVisitDate() : null;
    }

    private ClinicVisits pendingVisits() {
        ClinicVisits pendingVisits = new ClinicVisits();
        for (ClinicVisit clinicVisit : this) {
            DateTime appointmentDueDate = clinicVisit.getAppointmentDueDate();
            if (appointmentDueDate == null || appointmentDueDate.isBefore(DateUtil.now())) continue;
            pendingVisits.add(clinicVisit);
        }
        Collections.sort(pendingVisits);
        return pendingVisits;
    }

    private boolean hasVisits() {
        return this.size() > 0;
    }
}
