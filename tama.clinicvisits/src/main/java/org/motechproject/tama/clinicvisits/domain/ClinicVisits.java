package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.List;


public class ClinicVisits extends ArrayList<ClinicVisit> {

    public ClinicVisit getBaselineVisit() {
        for (ClinicVisit clinicVisit : this){
            if(clinicVisit.getTypeOfVisit() == ClinicVisit.TypeOfVisit.Baseline)
                return clinicVisit;
        }
        return null;
    }
}
