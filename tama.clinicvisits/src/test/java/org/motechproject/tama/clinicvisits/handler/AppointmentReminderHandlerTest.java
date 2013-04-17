package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.builder.ReminderEventBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.clinicvisits.service.AppointmentReminderService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentReminderHandlerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private AppointmentReminderService appointmentReminderService;

    private Patient patient;
    private ClinicVisit clinicVisit;

    private MotechEvent event;
    private AppointmentReminderHandler appointmentReminderHandler;

    public AppointmentReminderHandlerTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withId("visitId").build();
        event = ReminderEventBuilder.startRecording().withPatient(patient).withClinicVisit(clinicVisit).build();
    }

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderHandler = new AppointmentReminderHandler(allPatients, allClinicVisits, appointmentReminderService);
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allClinicVisits.get(patient.getId(), clinicVisit.getId())).thenReturn(clinicVisit);
    }

    @Test
    public void shouldRaiseAlert() {
        appointmentReminderHandler.handleEvent(event);
        verify(appointmentReminderService).raiseAlert(patient, clinicVisit);
    }
}
