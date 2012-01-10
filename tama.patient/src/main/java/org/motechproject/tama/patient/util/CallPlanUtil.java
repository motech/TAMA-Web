package org.motechproject.tama.patient.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

public class CallPlanUtil {

    public static LocalDate callPlanStartDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate treatmentStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        final DateTime callPreferenceTransitionDate = patient.getPatientPreferences().getCallPreferenceTransitionDate();
        if (callPreferenceTransitionDate != null && treatmentStartDate.isBefore(callPreferenceTransitionDate.toLocalDate()))
            treatmentStartDate = callPreferenceTransitionDate.toLocalDate();
        return treatmentStartDate;
    }

}
