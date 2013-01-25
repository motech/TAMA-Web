package org.motechproject.tama.patient.reporting;


import lombok.Data;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.motechproject.tama.patient.domain.CallPreference.DailyPillReminder;
import static org.motechproject.tama.patient.domain.CallPreference.FourDayRecall;

@Data
public class IVRDetails {

    private String ivrPassCode;
    private String callPreference;
    private String bestCallTime;
    private Boolean receiveOTCAdvice;
    private Boolean receiveAppointmentReminder;

    public IVRDetails(Patient patient) {
        ivrPassCode = patient.getPatientPreferences().getPasscode();
        callPreference = callPreference(patient);
        bestCallTime = bestCallTime(patient);
        receiveOTCAdvice = patient.hasAgreedToReceiveOTCAdvice();
        receiveAppointmentReminder = patient.getPatientPreferences().getActivateAppointmentReminders();
    }

    public IVRDetails(PatientRequest request) {
        ivrPassCode = request.getIvrPassCode();
        callPreference = request.getCallPreference();
        bestCallTime = request.getBestCallTime();
        receiveOTCAdvice = request.getReceiveOTCAdvice();
        receiveAppointmentReminder = request.getReceiveAppointmentReminder();
    }

    public void copyTo(PatientRequest request, String ivrLanguageCode) {
        request.setIvrLanguage(ivrLanguageCode);
        request.setIvrPassCode(ivrPassCode);
        request.setCallPreference(callPreference);
        request.setBestCallTime(bestCallTime);
        request.setReceiveOTCAdvice(receiveOTCAdvice);
        request.setReceiveAppointmentReminder(receiveAppointmentReminder);
    }

    private String bestCallTime(Patient patient) {
        TimeOfDay callTime = patient.getBestCallTime();
        return (null == callTime) ? "" : callTime.toString();
    }

    private String callPreference(Patient patient) {
        CallPreference preferences = patient.getPatientPreferences().getCallPreference();
        if (null == preferences) {
            return "";
        } else {
            return (patient.isOnDailyPillReminder()) ? DailyPillReminder.displayName() : FourDayRecall.displayName();
        }
    }
}
