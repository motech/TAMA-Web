package org.motechproject.tama.messages.message;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;
import static org.motechproject.util.DateUtil.today;

public class AppointmentReminderMessageTest {

    @Mock
    private Appointment appointment;
    @Mock
    private Patient patient;
    @Mock
    private Clinic clinic;
    @Mock
    private TAMAIVRContext tamaivrContext;

    private AppointmentReminderMessage appointmentReminderMessage;

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderMessage = new AppointmentReminderMessage(7, appointment, patient);

        when(patient.getClinic()).thenReturn(clinic);
        when(clinic.getPhone()).thenReturn("1234567890");
    }

    @Test
    public void shouldBeValidIfCurrentDateIsSevenDaysFromAppointmentDate() {
        LocalDate today = today();

        when(appointment.getDueDate()).thenReturn(today.plusDays(7));
        when(appointment.isUpcoming()).thenReturn(true);

        assertTrue(appointmentReminderMessage.isValid(today));
    }

    @Test
    public void shouldBeInValidIfCurrentDateIsEightDaysFromAppointmentDate() {
        LocalDate today = today();

        when(appointment.getDueDate()).thenReturn(today.plusDays(8));
        when(appointment.isUpcoming()).thenReturn(true);

        assertFalse(appointmentReminderMessage.isValid(today));
    }

    @Test
    public void shouldBeInvalidIfCurrentDateIsGreaterThanAppointmentDueDate() {
        LocalDate today = today();

        when(appointment.getDueDate()).thenReturn(today.minusDays(1));
        when(appointment.isUpcoming()).thenReturn(true);

        assertFalse(appointmentReminderMessage.isValid(today));
    }

    @Test
    public void shouldBeInvalidIfAppointmentIsAlreadyConfirmed() {
        DateTime now = now();

        when(appointment.getDueDate()).thenReturn(now.toLocalDate().plusDays(1));
        when(appointment.isUpcoming()).thenReturn(false);

        assertFalse(appointmentReminderMessage.isValid(now.toLocalDate()));
    }

    @Test
    public void shouldReturnPatientDocumentIdWithAppointmentDueDateAsUniqueId() {
        LocalDate dueDate = today().plusDays(1);
        when(appointment.getDueDate()).thenReturn(dueDate);
        assertEquals(patient.getId() + dueDate.toString(), appointmentReminderMessage.getId());
    }

    @Test
    public void shouldBuildAppointmentReminderMessage() {
        KookooIVRResponseBuilder response = appointmentReminderMessage.build(tamaivrContext);
        assertTrue(response.getPlayAudios().contains(TamaIVRMessage.NEXT_CLINIC_VISIT_IS_DUE_PART1));
    }
}
