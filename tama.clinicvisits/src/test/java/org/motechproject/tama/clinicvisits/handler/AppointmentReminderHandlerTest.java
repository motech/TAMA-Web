package org.motechproject.tama.clinicvisits.handler;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        AppointmentReminderHandlerTest.EventIsRaised.class
})
public class AppointmentReminderHandlerTest {

    public static class Basis {

        Appointment appointment;

        @Mock
        OutboxService outboxService;

        AppointmentReminderHandler appointmentReminderHandler;

        Basis() {
            initMocks(this);
            appointment = new Appointment();
            appointment.setExternalId("patientId");
            appointmentReminderHandler = new AppointmentReminderHandler(outboxService);
        }

        @Before
        public void setup() {
            reset(outboxService);
        }
    }

    public static class EventIsRaised extends Basis {

        MotechEvent appointmentReminderEvent;

        @Before
        public void setup() {
            super.setup();
            Map<String, Object> eventParams = new HashMap<String, Object>();
            eventParams.put(EventKeys.EXTERNAL_ID_KEY, appointment.getExternalId());
            appointmentReminderEvent = new MotechEvent(EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, eventParams);
        }

        @Test
        public void shouldCreateOutboxMessage() {
            appointmentReminderHandler.handleEvent(appointmentReminderEvent);
            verify(outboxService).addMessage(appointment.getExternalId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
        }
    }
}
