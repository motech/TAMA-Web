package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.appointments.api.service.contract.RescheduleAppointmentRequest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RescheduleAppointmentRequestBuilderTest {
    @Mock
    ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test
    public void shouldCreateRescheduleAppointmentRequest(){

        String patientId = "patientId";
        String clinicVisitId = "clinicVisitId";
        LocalDate adjustedDueDate = DateUtil.today();

        RescheduleAppointmentRequestBuilder rescheduleAppointmentRequestBuilder = new RescheduleAppointmentRequestBuilder(reminderConfigurationBuilder);

        ReminderConfiguration reminderConfiguration = new ReminderConfiguration();
        when(reminderConfigurationBuilder.newAppointmentReminder()).thenReturn(reminderConfiguration);

        RescheduleAppointmentRequest request = rescheduleAppointmentRequestBuilder.create(patientId, clinicVisitId, adjustedDueDate);

        assertEquals(patientId, request.getExternalId());
        assertEquals(clinicVisitId, request.getVisitName());
        assertEquals(DateUtil.newDateTime(adjustedDueDate), request.getAppointmentDueDate());
        assertEquals(1, request.getAppointmentReminderConfigurations().size());
        assertEquals(reminderConfiguration, request.getAppointmentReminderConfigurations().get(0));
    }
}
