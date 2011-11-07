package org.motechproject.tama.tools;

import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupPatient {

    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";
    private AllClinics allClinics;
    private AllPatients allPatients;
    private AllIVRLanguages allIVRLanguages;
    private AllModesOfTransmission allModesOfTransmission;
    private AllHIVTestReasons allHIVTestReasons;

    public SetupPatient() {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        allClinics = context.getBean(AllClinics.class);
        allPatients = context.getBean(AllPatients.class);
        allIVRLanguages = context.getBean(AllIVRLanguages.class);
        allModesOfTransmission = context.getBean(AllModesOfTransmission.class);
        allHIVTestReasons = context.getBean(AllHIVTestReasons.class);
    }

    public static void main(String[] args) {
        if (args.length != 9) {
            System.err.println("Invalid arguments!");
            return;
        }
        SetupPatient setup = new SetupPatient();
        setup.patient(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
    }

    private void patient(String clinicId, String patientId, String mobilePhoneNumber, String passcode,
                         String callPreference, String bestCallHour, String bestCallMinute, String languageCode, String dayOfWeeklyCall) {
        Clinic clinic = allClinics.get(clinicId);

        Patient patient = new Patient();

        patient.setClinic(clinic);
        patient.setPatientId(patientId);
        patient.setMobilePhoneNumber(mobilePhoneNumber);
        patient.setDateOfBirth(DateUtil.today().minusDays(10));
        patient.setGender(Gender.newGender("Female"));

        PatientPreferences patientPreferences = new PatientPreferences();
        patientPreferences.setPasscode(passcode);
        if ("Daily".equals(callPreference))
            patientPreferences.setCallPreference(CallPreference.DailyPillReminder);
        else
            patientPreferences.setCallPreference(CallPreference.FourDayRecall);

        patientPreferences.setBestCallTime(new TimeOfDay(Integer.parseInt(bestCallHour), Integer.parseInt(bestCallMinute), TimeMeridiem.AM));
        patientPreferences.setIvrLanguage(allIVRLanguages.findByLanguageCode(languageCode));
        patientPreferences.setDayOfWeeklyCall(DayOfWeek.valueOf(dayOfWeeklyCall));
        patient.setPatientPreferences(patientPreferences);

        MedicalHistory medicalHistory = new MedicalHistory();
        HIVMedicalHistory hivMedicalHistory = new HIVMedicalHistory();
        hivMedicalHistory.setModeOfTransmission(allModesOfTransmission.getAll().get(0));
        hivMedicalHistory.setTestReason(allHIVTestReasons.getAll().get(0));
        medicalHistory.setHivMedicalHistory(hivMedicalHistory);
        patient.setMedicalHistory(medicalHistory);

        patient.activate();

        allPatients.add(patient);
    }
}
