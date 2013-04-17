package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.builder.ReminderEventBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.clinicvisits.service.VisitReminderService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitReminderHandlerTest {

    public static final String PATIENT_ID = "patient_id";

    @Mock
    private VisitReminderService visitReminderService;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private AllPatients allPatients;

    private Patient patient;
    private MotechEvent event;
    public ClinicVisit clinicVisit;

    private VisitReminderHandler visitReminderHandler;

    public VisitReminderHandlerTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        clinicVisit = new ClinicVisitBuilder().withId("visitName").build();
        event = ReminderEventBuilder.startRecording().withPatient(patient).withClinicVisit(clinicVisit).build();
    }

    @Before
    public void setUp() {
        initMocks(this);

        visitReminderHandler = new VisitReminderHandler(allPatients, allClinicVisits, visitReminderService);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(allClinicVisits.get(PATIENT_ID, "visitName")).thenReturn(clinicVisit);
    }

    @Test
    public void shouldRaiseAlert() {
        visitReminderHandler.handleEvent(event);
        verify(visitReminderService).raiseAlert(patient, clinicVisit);
    }
}