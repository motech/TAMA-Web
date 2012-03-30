package org.motechproject.tama.patient.strategy;

import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;

import java.util.ArrayList;
import java.util.List;

public class ChangedPatientPreferenceContext {

    private Patient oldPatient;
    private Patient newPatient;

    public ChangedPatientPreferenceContext(Patient oldPatient, Patient newPatient) {
        this.oldPatient = oldPatient;
        this.newPatient = newPatient;
    }

    public boolean callPlanHasChanged() {
        final CallPreference oldValue = oldPatient == null ? null : oldPatient.callPreference();
        final CallPreference newValue = newPatient.callPreference();
        return oldValue != newValue;
    }

    public boolean dayOfCallHasChanged() {
        final DayOfWeek oldValue = oldPatient == null ? null : oldPatient.getDayOfWeeklyCall();
        final DayOfWeek newValue = newPatient.getDayOfWeeklyCall();
        return oldValue != newValue;
    }

    public boolean bestCallTimeHasChanged() {
        final TimeOfDay oldValue = oldPatient == null ? null : oldPatient.getBestCallTime();
        final TimeOfDay newValue = newPatient.getBestCallTime();
        return (oldValue != null && oldValue.toTime() != null && !oldValue.equals(newValue)) || (newValue != null && newValue.toTime() != null && !newValue.equals(oldValue));
    }

    public boolean patientPreferenceHasChanged() {
        return bestCallTimeHasChanged() || callPlanHasChanged() || dayOfCallHasChanged();
    }

    public List<PatientEventLog> getEventLogs() {
        final ArrayList<PatientEventLog> patientEventLogs = new ArrayList<PatientEventLog>();
        if (callPlanHasChanged()) {
            final CallPreference callPreference = newPatient.getPatientPreferences().getCallPreference();
            String newValue = callPreference == null ? "Value was unset" : callPreference.name();
            patientEventLogs.add(new PatientEventLog(newPatient.getId(), PatientEvent.Call_Plan_Changed, newValue));
        }
        if (dayOfCallHasChanged()) {
            final DayOfWeek dayOfCall = newPatient.getPatientPreferences().getDayOfWeeklyCall();
            String newValue = dayOfCall == null ? "Value was unset" : dayOfCall.name();
            patientEventLogs.add(new PatientEventLog(newPatient.getId(), PatientEvent.Day_Of_Weekly_Call_Changed, newValue));
        }
        if (bestCallTimeHasChanged()) {
            final TimeOfDay bestCallTime = newPatient.getPatientPreferences().getBestCallTime();
            String newValue = (bestCallTime == null || bestCallTime.toTime() == null) ? "Value was unset" : bestCallTime.toString();
            patientEventLogs.add(new PatientEventLog(newPatient.getId(), PatientEvent.Best_Call_Time_Changed, newValue));
        }
        return patientEventLogs;
    }
}
