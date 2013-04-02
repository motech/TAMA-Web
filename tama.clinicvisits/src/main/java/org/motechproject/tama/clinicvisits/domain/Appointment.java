package org.motechproject.tama.clinicvisits.domain;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Data
public class Appointment {

    private DateTime visitDate;
    private LocalDate dueDate;
    private DateTime confirmedDate;

    public Appointment(ClinicVisit visitResponse) {
        dueDate = visitResponse.getEffectiveDueDate();
        confirmedDate = visitResponse.getConfirmedAppointmentDate();
        visitDate = visitResponse.getVisitDate();
    }

    public boolean isUpcoming() {
        return null == confirmedDate && null == visitDate;
    }

    public boolean isOnOrAfter(LocalDate reference) {
        return !dueDate.isBefore(reference);
    }
}
