package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.clinicvisits.builder.AppointmentBuilder;
import org.motechproject.tama.clinicvisits.builder.ReminderEventBuilder;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
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
    private AllAppointments allAppointments;
    @Mock
    private AllPatients allPatients;

    private Patient patient;
    private Appointment appointment;
    private MotechEvent event;
    public Visit visit;

    private VisitReminderHandler visitReminderHandler;

    public VisitReminderHandlerTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        appointment = AppointmentBuilder.startRecording().build();
        visit = new Visit().name("visitName");
        event = ReminderEventBuilder.startRecording().withAppointment(appointment).withPatient(patient).withVisit(visit).build();
    }

    @Before
    public void setUp() {
        initMocks(this);

        visitReminderHandler = new VisitReminderHandler(allPatients, allAppointments, visitReminderService);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(allAppointments.get(appointment.id())).thenReturn(appointment);
    }

    @Test
    public void shouldRaiseVisitOutboxMessage() {
        visitReminderHandler.handleEvent(event);
        verify(visitReminderService).addOutboxMessage(patient, appointment, visit.name());
    }
}