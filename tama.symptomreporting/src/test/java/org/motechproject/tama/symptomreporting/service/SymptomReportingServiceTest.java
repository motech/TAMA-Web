package org.motechproject.tama.symptomreporting.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
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
import org.motechproject.tama.symptomreporting.testdata.PatientSetup;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingServiceTest extends BaseUnitTest {

    public static final String LAB_RESULT_ID = "labResultId";

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
    @Mock
    private AllClinicVisits allClinicVisits;

    private final String patientId = "patientId";

    private final String patientDocId = "patientDocId";

    private String clinicId = UUID.randomUUID().toString();

    private final SendSMSRequest additionalRequest1 = new SendSMSRequest("adr1", StringUtils.EMPTY);
    private final SendSMSRequest additionalRequest2 = new SendSMSRequest("adr2", StringUtils.EMPTY);

    private SymptomReportingService symptomReportingService;

    @Before
    public void setUp() {
        initMocks(this);
        setupVisits();
        when(clinicianSMSProperties.get("additional_sms_numbers")).thenReturn(additionalRequest1.getRecipientNumber() + "," + additionalRequest2.getRecipientNumber());
        symptomReportingService = new SymptomReportingService(allPatients, allTreatmentAdvices, allLabResults, allRegimens, allVitalStatistics, allSymptomReports, kookooCallDetailRecordsService, sendSMSService, clinicianSMSProperties, symptomReportingProperties, allClinicVisits);
    }

    private void setupVisits() {
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().withId(patientDocId).build(), new VisitResponse());
        clinicVisit.setLabResultIds(asList(LAB_RESULT_ID));

        when(allClinicVisits.getBaselineVisit(patientDocId)).thenReturn(clinicVisit);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withId(patientDocId).withPatientId(patientId).withDateOfBirth(new LocalDate(2000, 10, 1)).build();
        mockCurrentDate(new LocalDate(2011, 11, 1));
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withId(LAB_RESULT_ID).withPatientId(patientId).withLabTestId(labTestId).withResult("60").build();
        labResult.setLabTest(labTest);
        when(allLabResults.withIds(any(List.class))).thenReturn(asList(labResult));

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
        Clinic clinic = ClinicBuilder.startRecording().withName("pujari").withId(clinicId).build();
        clinic.setClinicianContacts(Arrays.asList(
                new Clinic.ClinicianContact("name1", "ph1")
        ));


        Patient patient = PatientBuilder.startRecording().withId(patientDocId).withClinic(clinic).withMobileNumber("1234567890").withPatientId(patientId).build();
        Regimen regimen = mock(Regimen.class);

        SymptomReport symptomReport = mock(SymptomReport.class);

        SendSMSRequest request1 = new SendSMSRequest("ph1", clinic.getClinicianContacts().get(0).getId());
        SendSMSRequest request2 = new SendSMSRequest("ph2", clinic.getClinicianContacts().get(0).getId());
        SendSMSRequest request3 = new SendSMSRequest("ph3", clinic.getClinicianContacts().get(0).getId());

        when(regimen.getDisplayName()).thenReturn("D4T+EFV+NVP");
        when(symptomReport.getAdviceGiven()).thenReturn("adv_crocin01");
        when(clinicianSMSProperties.get("adv_crocin01")).thenReturn("ADV: Some advice");
        when(symptomReport.getSymptomIds()).thenReturn(Arrays.asList("fever", "nauseavomiting", "headache"));
        when(symptomReportingProperties.get("fever")).thenReturn("Fever");
        when(symptomReportingProperties.get("nauseavomiting")).thenReturn("Nausea or Vomiting");
        when(symptomReportingProperties.get("headache")).thenReturn("Headache");

        symptomReportingService.notifyCliniciansAboutOTCAdvice(patient, regimen, Arrays.asList(request1, request2, request3), symptomReport);

        verify(sendSMSService).send(Arrays.asList(request1, request2, request3), "patientId (pujari):1234567890:D4T+EFV+NVP, trying to contact. Fever,Nausea or Vomiting,Headache. ADV: Some advice", SMSType.Clinician);
        verify(sendSMSService).send(Arrays.asList(additionalRequest1, additionalRequest2), "patientId (pujari):1234567890:D4T+EFV+NVP, trying to contact. Fever,Nausea or Vomiting,Headache. ADV: Some advice", SMSType.AdditionalSMS);
    }

    @Test
    public void shouldSMS_OTCAdviceToAllCliniciansInAGivenClinic_WhenDialToAllCliniciansFails() {
        Clinic clinic = new Clinic(clinicId);

        clinic.setClinicianContacts(Arrays.asList(
                new Clinic.ClinicianContact("name1", "ph1"),
                new Clinic.ClinicianContact("name2", "ph2")));

        SendSMSRequest request1 = new SendSMSRequest("ph1", clinic.getClinicianContacts().get(0).getId());
        SendSMSRequest request2 = new SendSMSRequest("ph2", clinic.getClinicianContacts().get(1).getId());

        PatientSetup setup = new PatientSetup(allPatients, allTreatmentAdvices, allRegimens)
                .withClinic(clinic)
                .withMobileNumber("1234567890")
                .withPatientDocumentId(patientDocId)
                .withPatientId(patientId)
                .withRegimenId("regimenId");
        setup.run();

        KookooCallDetailRecord callDetailRecord = new KookooCallDetailRecord(null, "callId");
        when(kookooCallDetailRecordsService.get("callId")).thenReturn(callDetailRecord);

        SymptomReport symptomReport = mock(SymptomReport.class);
        when(allSymptomReports.findByCallId("callId")).thenReturn(symptomReport);
        when(symptomReport.getDoctorContacted()).thenReturn(TAMAConstants.ReportedType.No);

        symptomReportingService = Mockito.spy(symptomReportingService);
        doNothing().when(symptomReportingService).notifyCliniciansAboutOTCAdvice(setup.getPatient(), setup.getRegimen(), Arrays.asList(request1, request2), symptomReport);
        doCallRealMethod().when(symptomReportingService).smsOTCAdviceToAllClinicians(patientDocId, "callId");

        symptomReportingService.smsOTCAdviceToAllClinicians(patientDocId, "callId");

        verify(symptomReportingService).notifyCliniciansAboutOTCAdvice(setup.getPatient(), setup.getRegimen(), Arrays.asList(request1, request2), symptomReport);
    }

}