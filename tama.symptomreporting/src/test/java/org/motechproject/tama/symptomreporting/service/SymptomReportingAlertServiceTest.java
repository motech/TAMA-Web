package org.motechproject.tama.symptomreporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.repository.AllAuditEvents;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertSearchService;
import org.motechproject.tama.patient.service.PatientAlertService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingAlertServiceTest {

    @Mock
    private AlertService alertService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private PatientAlertSearchService patientAlertSearchService;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private AllAuditEvents allAuditEvents;

    private SymptomReportingAlertService symptomReportingAlertService;

    @Before
    public void setUp() {
        initMocks(this);
        symptomReportingAlertService = new SymptomReportingAlertService(allPatients, alertService, patientAlertSearchService, allAuditEvents, patientAlertService);
    }

    @Test
    public void shouldUpdateDoctorConnectedToDuringSymptomCall() {
        final String patientDocId = "patientDocId";
        final String alertId = "alertId";
        String doctorName = "kumarasamy";

        final Patient patient = new Patient() {{
            setPatientId(patientDocId);
            setId(patientDocId);
        }};

        final HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());

        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(patientDocId, AlertType.MEDIUM, AlertStatus.NEW, 2, null) {{
                setData(data);
                setId(alertId);
            }}, patient));
        }};

        when(allPatients.get(patientDocId)).thenReturn(patient);
        when(patientAlertSearchService.search(patientDocId)).thenReturn(alerts);

        symptomReportingAlertService.updateDoctorConnectedToDuringSymptomCall(patientDocId, doctorName);

        Map<String, String> updatedData = new HashMap<String, String>();
        updatedData.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        updatedData.put(PatientAlert.DOCTOR_NAME, doctorName);
        verify(patientAlertService, times(1)).updateData(Matchers.<Alert>any(), eq(updatedData), eq(patientDocId));
    }

    @Test
    public void shouldAppendTheFirstSymptomReportedToAlert() {
        final Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        final Alert alert = new Alert(patient.getId(), "", "", AlertType.MEDIUM, AlertStatus.READ, 2, data);
        alert.setId("alertId");

        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(alert, patient));
        }};

        when(patientAlertSearchService.search(patient.getId())).thenReturn(alerts);
        symptomReportingAlertService.appendSymptomToAlert(patient.getId(), "fever");

        verify(alertService, times(1)).update(eq(alert.getId()), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().status(AlertStatus.NEW).description("fever"))));
    }

    @Test
    public void shouldAppendSubsequentSymptomReportedToAlert() {
        final Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();

        HashMap<String, String> data = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }};
        final Alert alert = new Alert(patient.getId(), "", "nausea", AlertType.MEDIUM, AlertStatus.READ, 2, data);
        alert.setId("alertId");

        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(alert, patient));
        }};

        when(patientAlertSearchService.search(patient.getId())).thenReturn(alerts);
        symptomReportingAlertService.appendSymptomToAlert(patient.getId(), "fever");

        verify(alertService, times(1)).update(eq(alert.getId()), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().status(AlertStatus.NEW).description("nausea, fever"))));
    }

    @Test
    public void shouldCreateSymptomsReportingAlert() {
        String patientDocId = "patientDocId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocId).build();
        when(allPatients.get(patientDocId)).thenReturn(patient);

        symptomReportingAlertService.createSymptomsReportingAlert(patientDocId);

        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        data.put(PatientAlert.PATIENT_CALL_PREFERENCE, patient.getPatientPreferences().getDisplayCallPreference());
        data.put(PatientAlert.SYMPTOMS_ALERT_STATUS, SymptomsAlertStatus.Open.name());
        data.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.NA.name());
        verify(alertService).create(patientDocId, "", "", AlertType.MEDIUM, AlertStatus.NEW, TAMAConstants.NO_ALERT_PRIORITY, data);
    }

    @Test
    public void shouldSetConnectedToDoctorStatusOnSymptomsAlert() {
        String patientDocId = "patientDocId", alertId = "alertId";
        final Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocId).build();

        HashMap<String, String> data = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }};

        final Alert alert = new Alert(patient.getId(), "", "nausea", AlertType.MEDIUM, AlertStatus.READ, 2, data);
        alert.setId(alertId);

        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(alert, patient));
        }};
        when(patientAlertSearchService.search(patient.getId())).thenReturn(alerts);

        symptomReportingAlertService.setConnectedToDoctorStatusOnSymptomsReportingAlert(patientDocId, TAMAConstants.ReportedType.No);
        HashMap<String, String> newData = new HashMap<String, String>();
        newData.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.No.name());
        verify(alertService, times(1)).update(eq(alert.getId()), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().status(AlertStatus.NEW).data(newData))));
    }

    @Test
    public void shouldUpdateAdviceAndPriorityOnSymptomsAlert() {
        String patientDocId = "patientDocId", alertId = "alertId";
        final Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocId).build();

        HashMap<String, String> data = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }};

        final Alert alert = new Alert(patient.getId(), "", "nausea", AlertType.MEDIUM, AlertStatus.READ, 1, data);
        alert.setId(alertId);

        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(alert, patient));
        }};
        when(patientAlertSearchService.search(patient.getId())).thenReturn(alerts);

        symptomReportingAlertService.updateAdviceOnSymptomsReportingAlert(patientDocId, "Some advice", 2);
        verify(alertService, times(1)).update(eq(alert.getId()), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().status(AlertStatus.NEW).priority(2).name("Some advice"))));
    }

    private class UpdateCriteriaMatcher extends ArgumentMatcher<UpdateCriteria> {
        private UpdateCriteria updateCriteria;

        private UpdateCriteriaMatcher(UpdateCriteria updateCriteria) {
            this.updateCriteria = updateCriteria;
        }

        @Override
        public boolean matches(Object argument) {
            UpdateCriteria other = (UpdateCriteria) argument;
            return updateCriteria.getAll().equals(other.getAll());
        }
    }
}
