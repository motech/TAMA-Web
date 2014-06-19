package org.motechproject.tama.symptomreporting.service;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.mapper.MedicalConditionsMapper;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class SymptomReportingService {
    private AllPatients allPatients;
    private AllVitalStatistics allVitalStatistics;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllLabResults allLabResults;
    private AllRegimensCache allRegimens;
    private Properties clinicianSMSProperties;
    private Properties symptomReportingProperties;
    private AllSymptomReports allSymptomReports;
    private AllClinicVisits allClinicVisits;
    private SendSMSService sendSMSService;
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    @Autowired
    public SymptomReportingService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                   AllLabResults allLabResults, AllRegimensCache allRegimens,
                                   AllVitalStatistics allVitalStatistics,
                                   AllSymptomReports allSymptomReports, KookooCallDetailRecordsService kookooCallDetailRecordsService,
                                   SendSMSService sendSMSService,
                                   @Qualifier("clinicianSMSProperties") Properties clinicianSMSProperties,
                                   @Qualifier("symptomProperties") Properties symptomReportingProperties,
                                   AllClinicVisits allClinicVisits) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
        this.allVitalStatistics = allVitalStatistics;
        this.allSymptomReports = allSymptomReports;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.sendSMSService = sendSMSService;
        this.clinicianSMSProperties = clinicianSMSProperties;
        this.symptomReportingProperties = symptomReportingProperties;
        this.allClinicVisits = allClinicVisits;
    }

    public MedicalCondition getPatientMedicalConditions(String patientId) {
        Patient patient = allPatients.get(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Regimen currentRegimen = allRegimens.getBy(currentTreatmentAdvice.getRegimenId());

        return new MedicalConditionsMapper(patient, baselineCD4Count(patient), vitalStatistics, earliestTreatmentAdvice, currentRegimen).map();
    }

    private LabResult baselineCD4Count(Patient patient) {
        ClinicVisit baselineVisit = allClinicVisits.getBaselineVisit(patient.getId());
        LabResults results = new LabResults(allLabResults.withIds(baselineVisit.getLabResultIds()));
        return results.latestResultOf(TAMAConstants.LabTestType.CD4);
    }

    public void smsOTCAdviceToAllClinicians(String patientDocId, String callLogDocId) {
        KookooCallDetailRecord kookooCallDetailRecord = kookooCallDetailRecordsService.get(callLogDocId);
        SymptomReport symptomReport = allSymptomReports.findByCallId(kookooCallDetailRecord.getVendorCallId());
        Patient patient = allPatients.get(patientDocId);
        Regimen regimen = allRegimens.getBy(allTreatmentAdvices.currentTreatmentAdvice(patientDocId).getRegimenId());
        List<SendSMSRequest> cliniciansMobileNumbers = new ArrayList<>();
        for (Clinic.ClinicianContact clinicianContact : patient.getClinic().getClinicianContacts()) {
            cliniciansMobileNumbers.add(new SendSMSRequest(clinicianContact.getPhoneNumber(), clinicianContact.getId()));
        }
        notifyCliniciansAboutOTCAdvice(patient, regimen, cliniciansMobileNumbers, symptomReport);
    }

    void notifyCliniciansAboutOTCAdvice(Patient patient, Regimen regimen, List<SendSMSRequest> clinicianContacts, SymptomReport symptomReport) {
        List<String> symptoms = new ArrayList<>();

        for (String symptomId : symptomReport.getSymptomIds()) {
            symptoms.add(((String) symptomReportingProperties.get(symptomId)));
        }

        String symptomsReported = StringUtils.join(symptoms, ",");
        String adviceGiven = fullAdviceGiven(symptomReport.getAdviceGiven());
        String message = String.format("%s (%s):%s:%s, trying to contact. %s. %s", patient.getPatientId(), patient.getClinic().getName(), patient.getMobilePhoneNumber(), regimen.getDisplayName(), symptomsReported, adviceGiven);
        sendSmsToAll(message, clinicianContacts, patient);
    }
    
    public void sendSmsToAll(String message,List<SendSMSRequest> clinicianContacts,Patient patient){
    	MonitoringAgent monitoringAgent = (patient.getClinic().getMonitoringAgent());
    	SendSMSRequest monitoringAgentsMobileNumber = new SendSMSRequest(monitoringAgent.getContactNumber(),monitoringAgent.getId());
    	sendSMSService.send(clinicianContacts, message, SMSType.Clinician);
    	sendSMSService.send(monitoringAgentsMobileNumber, message, SMSType.MonitoringAgent);
        sendSMSService.send(additionalNumbersToSendSMS(), message, SMSType.AdditionalSMS);
    }

    private List<SendSMSRequest> additionalNumbersToSendSMS() {
        String additionalSMSNumbers = (String) clinicianSMSProperties.get("additional_sms_numbers");
        if (StringUtils.isEmpty(additionalSMSNumbers)) {
            return Collections.emptyList();
        } else {
            List<String> additionalNumbers = Arrays.asList(additionalSMSNumbers.replaceAll(" ", "").split(","));
            List<SendSMSRequest> requests = new ArrayList<>();
            for (String additionalNumber : additionalNumbers) {
                requests.add(new SendSMSRequest(additionalNumber));
            }
            return requests;
        }
    }

    public String fullAdviceGiven(String adviceGiven) {
        return (String) clinicianSMSProperties.get(adviceGiven);
    }
}
