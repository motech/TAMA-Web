package org.motechproject.tama.symptomreporting.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.mapper.MedicalConditionsMapper;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class SymptomReportingService {
    private AllPatients allPatients;
    private AllVitalStatistics allVitalStatistics;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllLabResults allLabResults;
    private AllRegimens allRegimens;
    private Properties symptomReportingAdviceMap;
    private AllSymptomReports allSymptomReports;
    private SendSMSService sendSMSService;

    @Autowired
    public SymptomReportingService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                   AllLabResults allLabResults, AllRegimens allRegimens,
                                   AllVitalStatistics allVitalStatistics,
                                   AllSymptomReports allSymptomReports, SendSMSService sendSMSService,
                                   @Qualifier("adviceMap") Properties symptomReportingAdviceMap) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
        this.allVitalStatistics = allVitalStatistics;
        this.allSymptomReports = allSymptomReports;
        this.sendSMSService = sendSMSService;
        this.symptomReportingAdviceMap = symptomReportingAdviceMap;
    }

    public MedicalCondition getPatientMedicalConditions(String patientId) {
        Patient patient = allPatients.get(patientId);
        LabResults labResults = allLabResults.findLatestLabResultsByPatientId(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Regimen currentRegimen = allRegimens.get(currentTreatmentAdvice.getRegimenId());

        return new MedicalConditionsMapper(patient, labResults, vitalStatistics, earliestTreatmentAdvice, currentRegimen).map();
    }

    public void notifyCliniciansIfCallMissed(String callId, String patientDocId) {
        SymptomReport symptomReport = allSymptomReports.findByCallId(callId);
        if (symptomReport.getDoctorContacted().equals(TAMAConstants.ReportedType.No)) {
            notifyCliniciansAboutOTCAdvice(patientDocId, symptomReport);
        }
    }

    public void notifyCliniciansAboutOTCAdvice(String patientDocId, SymptomReport symptomReport) {
        Patient patient = allPatients.get(patientDocId);
        Regimen regimen = allRegimens.get(allTreatmentAdvices.currentTreatmentAdvice(patientDocId).getRegimenId());

        String symptomsReported = StringUtils.join(symptomReport.getSymptomIds(), ",");
        String adviceGiven = fullAdviceGiven(symptomReport.getAdviceGiven());

        List<Clinic.ClinicianContact> clinicianContacts = patient.getClinic().getClinicianContacts();
        List<String> cliniciansMobileNumbers = new ArrayList<String>();
        for (Clinic.ClinicianContact clinicianContact : clinicianContacts) {
            cliniciansMobileNumbers.add(clinicianContact.getPhoneNumber());
        }

        String message = patient.getPatientId() + ":" + patient.getMobilePhoneNumber() + ":" + regimen.getDisplayName() + ",trying to contact. " + symptomsReported + ". " + adviceGiven;

        sendSMSService.send(cliniciansMobileNumbers, message);
    }

    public String fullAdviceGiven(String adviceGiven) {
        return (String) symptomReportingAdviceMap.get(adviceGiven);
    }
}
