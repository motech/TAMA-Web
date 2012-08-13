package org.motechproject.tama.symptomreporting.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingServiceTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllRegimensCache allRegimens;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private Properties clinicianSMSProperties;
    @Mock
    private Properties symptomReportingProperties;
    @Mock
    private AllSymptomReports allSymptomReports;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    private final String patientId = "patientId";

    private SymptomReportingService symptomReportingService;

    @Before
    public void setUp() {
        initMocks(this);
        when(clinicianSMSProperties.get("additional_sms_numbers")).thenReturn("0000000000, 1111111111, ");
        symptomReportingService = new SymptomReportingService(allPatients, allTreatmentAdvices, allLabResults, allRegimens, allVitalStatistics, allSymptomReports, kookooCallDetailRecordsService, sendSMSService, clinicianSMSProperties, symptomReportingProperties);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {

        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(new LocalDate(2000, 10, 1)).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTestId(labTestId).withResult("60").build();
        labResult.setLabTest(labTest);
        when(allLabResults.allLabResults(patientId)).thenReturn(new LabResults(Arrays.asList(labResult)));

        when(allVitalStatistics.findLatestVitalStatisticByPatientId(patientId)).thenReturn(new VitalStatistics(74.00, 174.00, 10, 10, 10.00, 10, patientId));

        String regimenId = "regimenId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(DateUtil.today()).withRegimenId(regimenId).build();
        when(allTreatmentAdvices.earliestTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

        String regimenName = "Regimen Name";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();
        when(allRegimens.getBy(regimenId)).thenReturn(regimen);

        MedicalCondition medicalCondition = symptomReportingService.getPatientMedicalConditions(patientId);

        assertEquals(regimenName, medicalCondition.regimenName());
        assertEquals("Male", medicalCondition.gender());
        assertEquals(24.44, medicalCondition.bmi());
        assertEquals(11, medicalCondition.age());
        assertEquals(60, medicalCondition.cd4Count());
    }

    @Test
    public void shouldSendSMSToNotifyCliniciansAboutOTCAdvice() {
        Clinic clinic = ClinicBuilder.startRecording().withName("pujari").build();
        Patient patient = PatientBuilder.startRecording().withClinic(clinic).withMobileNumber("1234567890").withPatientId(patientId).build();
        Regimen regimen = mock(Regimen.class);
        SymptomReport symptomReport = mock(SymptomReport.class);
        when(regimen.getDisplayName()).thenReturn("D4T+EFV+NVP");
        when(symptomReport.getAdviceGiven()).thenReturn("adv_crocin01");
        when(clinicianSMSProperties.get("adv_crocin01")).thenReturn("ADV: Some advice");
        when(symptomReport.getSymptomIds()).thenReturn(Arrays.asList("fever", "nauseavomiting", "headache"));
        when(symptomReportingProperties.get("fever")).thenReturn("Fever");
        when(symptomReportingProperties.get("nauseavomiting")).thenReturn("Nausea or Vomiting");
        when(symptomReportingProperties.get("headache")).thenReturn("Headache");

        symptomReportingService.notifyCliniciansAboutOTCAdvice(patient, regimen, Arrays.asList("ph1", "ph2", "ph3"), symptomReport);

        verify(sendSMSService).send(Arrays.asList("ph1", "ph2", "ph3", "0000000000", "1111111111"), "patientId (pujari):1234567890:D4T+EFV+NVP, trying to contact. Fever,Nausea or Vomiting,Headache. ADV: Some advice");
    }

    @Test
    public void shouldSMS_OTCAdviceToAllCliniciansInAGivenClinic_WhenDialToAllCliniciansFails() {
        final Clinic clinic = new Clinic("id");
        clinic.setClinicianContacts(Arrays.asList(
                new Clinic.ClinicianContact("name1", "ph1"),
                new Clinic.ClinicianContact("name2", "ph2")));

        String patientDocId = "patientDocId";
        Patient patient = PatientBuilder.startRecording().withMobileNumber("1234567890").withId(patientDocId).withPatientId(patientId).withClinic(clinic).build();
        when(allPatients.get(patientDocId)).thenReturn(patient);
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withRegimenId("regimenId").build();
        when(allTreatmentAdvices.currentTreatmentAdvice(patientDocId)).thenReturn(treatmentAdvice);
        Regimen regimen = mock(Regimen.class);
        when(allRegimens.getBy(Matchers.<String>any())).thenReturn(regimen);

        KookooCallDetailRecord callDetailRecord = new KookooCallDetailRecord(null, "callId");
        when(kookooCallDetailRecordsService.get("callId")).thenReturn(callDetailRecord);

        SymptomReport symptomReport = mock(SymptomReport.class);
        when(allSymptomReports.findByCallId("callId")).thenReturn(symptomReport);
        when(symptomReport.getDoctorContacted()).thenReturn(TAMAConstants.ReportedType.No);

        symptomReportingService = Mockito.spy(symptomReportingService);
        doNothing().when(symptomReportingService).notifyCliniciansAboutOTCAdvice(patient, regimen, Arrays.asList("ph1", "ph2"), symptomReport);
        doCallRealMethod().when(symptomReportingService).smsOTCAdviceToAllClinicians(patientDocId, "callId");

        symptomReportingService.smsOTCAdviceToAllClinicians(patientDocId, "callId");

        verify(symptomReportingService).notifyCliniciansAboutOTCAdvice(patient, regimen, Arrays.asList("ph1", "ph2"), symptomReport);
    }

}