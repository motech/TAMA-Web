package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ClinicVisits extends ArrayList<ClinicVisit> {

    public ClinicVisits() {

    }

    public ClinicVisits(Collection<? extends ClinicVisit> c) {
        super(c);
    }

    public ClinicVisits(Patient patient, List<VisitResponse> responses) {
        for (VisitResponse response : responses) {
            this.add(new ClinicVisit(patient, response));
        }
    }

    public Appointment upcomingAppointment(LocalDate reference) {
        ClinicVisits pendingVisits = pendingVisits();
        Appointment appointment = null;
        for (ClinicVisit pendingVisit : pendingVisits) {
            appointment = new Appointment(pendingVisit);
            if (appointment.isOnOrAfter(reference) && appointment.isUpcoming()) {
                return appointment;
            }
        }
        return appointment;
    }

    public ClinicVisit upcomingVisit(DateTime reference) {
        sortedVisits(this);
        for (ClinicVisit visit : this) {
            if (visit.isOnOrAfter(reference) && visit.isUpcoming()) {
                return visit;
            }
        }
        return null;
    }

    public LocalDate nextAppointmentDueDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        if (!allPendingVisits.hasVisits()) return null;
        return allPendingVisits.get(0).getEffectiveDueDate();
    }

    public LocalDate nextConfirmedAppointmentDate() {
        ClinicVisits allPendingVisits = pendingVisits();
        if (!allPendingVisits.hasVisits()) return null;
        DateTime confirmedAppointmentDate = allPendingVisits.get(0).getConfirmedAppointmentDate();
        return DateUtil.newDate(confirmedAppointmentDate);
    }

    private ClinicVisits pendingVisits() {
        ClinicVisits pendingVisits = new ClinicVisits();
        for (ClinicVisit clinicVisit : this) {
            LocalDate effectiveDueDate = clinicVisit.getEffectiveDueDate();
            if (effectiveDueDate == null || effectiveDueDate.isBefore(DateUtil.today())) continue;
            pendingVisits.add(clinicVisit);
        }
        sortedVisits(pendingVisits);
        return pendingVisits;
    }

    private void sortedVisits(ClinicVisits pendingVisits) {
        Collections.sort(pendingVisits);
    }

    private boolean hasVisits() {
        return this.size() > 0;
    }
}
