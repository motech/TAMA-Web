package org.motechproject.tama.patient.reporting;


import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.common.util.TimeUtil;
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
    private String ivrLanguageCode;
    private String gender;

    public IVRDetails(Patient patient, String ivrLanguageCode, String gender) {
        this.ivrLanguageCode = ivrLanguageCode;
        this.gender = gender;
        ivrPassCode = patient.getPatientPreferences().getPasscode();
        callPreference = callPreference(patient);
        bestCallTime = bestCallTime(patient);
        receiveOTCAdvice = patient.hasAgreedToReceiveOTCAdvice();
        receiveAppointmentReminder = patient.getPatientPreferences().getActivateAppointmentReminders();
    }

    public IVRDetails(PatientRequest request) {
        ivrLanguageCode = request.getIvrLanguage();
        gender = request.getGender();
        ivrPassCode = request.getIvrPassCode();
        callPreference = request.getCallPreference();
        bestCallTime = request.getBestCallTime();
        receiveOTCAdvice = request.getReceiveOTCAdvice();
        receiveAppointmentReminder = request.getReceiveAppointmentReminder();
    }

    public void copyTo(PatientRequest request) {
        request.setIvrLanguage(ivrLanguageCode);
        request.setGender(gender);
        request.setIvrPassCode(ivrPassCode);
        request.setCallPreference(callPreference);
        mapBestCallTime(request);
        request.setReceiveOTCAdvice(receiveOTCAdvice);
        request.setReceiveAppointmentReminder(receiveAppointmentReminder);
    }

    private void mapBestCallTime(PatientRequest request) {
        if (StringUtils.isNotBlank(bestCallTime)) {
            request.setBestCallTime(new TimeUtil(bestCallTime).toTimeStamp());
        } else {
            request.setBestCallTime("");
        }
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
