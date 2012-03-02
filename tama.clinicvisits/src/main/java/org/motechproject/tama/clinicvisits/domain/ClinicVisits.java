package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;


public class ClinicVisits extends ArrayList<ClinicVisit> {

    public LocalDate nextAppointmentDueDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        if (!allPendingVisits.hasVisits()) return null;
        return allPendingVisits.get(0).getEffectiveDueDate();
    }

    public LocalDate nextConfirmedAppointmentDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        if (!allPendingVisits.hasVisits()) return null;
        DateTime confirmedVisitDate = allPendingVisits.get(0).getConfirmedAppointmentDate();
        return confirmedVisitDate == null ? null : confirmedVisitDate.toLocalDate();
    }

    private ClinicVisits pendingVisits() {
        ClinicVisits pendingVisits = new ClinicVisits();
        for (ClinicVisit clinicVisit : this) {
            LocalDate effectiveDueDate = clinicVisit.getEffectiveDueDate();
            if (effectiveDueDate == null || effectiveDueDate.isBefore(DateUtil.today())) continue;
            pendingVisits.add(clinicVisit);
        }
        Collections.sort(pendingVisits);
        return pendingVisits;
    }

    private boolean hasVisits() {
        return this.size() > 0;
    }
}
