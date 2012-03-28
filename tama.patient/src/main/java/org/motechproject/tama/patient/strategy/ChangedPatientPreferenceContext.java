package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;

public class ChangedPatientPreferenceContext {

    private Patient oldPatient;
    private Patient newPatient;

    public ChangedPatientPreferenceContext(Patient oldPatient, Patient newPatient) {
        this.oldPatient = oldPatient;
        this.newPatient = newPatient;
    }

    public boolean callPlanHasChanged() {
        return !oldPatient.callPreference().equals(newPatient.callPreference());
    }

    public boolean dayOfCallHasChanged() {
        return oldPatient.getDayOfWeeklyCall() != null && !oldPatient.getDayOfWeeklyCall().equals(newPatient.getDayOfWeeklyCall());
    }

    public boolean bestCallTimeHasChanged() {
        return oldPatient.getBestCallTime() != null && !oldPatient.getBestCallTime().equals(newPatient.getBestCallTime());
    }

    public boolean patientPreferenceHasChanged() {
        return bestCallTimeHasChanged() || callPlanHasChanged() || dayOfCallHasChanged();
    }

}
