package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CreateVisitRequestBuilderTest {

    private CreateVisitRequestBuilder createVisitRequestBuilder;

    public CreateVisitRequestBuilderTest() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FROM, "10");
        ReminderConfigurationBuilder reminderConfigurationBuilder = new ReminderConfigurationBuilder(appointmentsProperties);
        createVisitRequestBuilder = new CreateVisitRequestBuilder(reminderConfigurationBuilder);
    }

    @Test
    public void shouldCreateVisitRequestGivenDueDate() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        CreateVisitRequest visitRequest = createVisitRequestBuilder.adHocVisitRequestForToday("visitName", TypeOfVisit.Unscheduled, dueDate);
        assertEquals(dueDate, visitRequest.getAppointmentDueDate());
        assertEquals(TypeOfVisit.Unscheduled.toString(), visitRequest.getTypeOfVisit());
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingDueDate() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        CreateVisitRequest visitRequest = createVisitRequestBuilder.adHocVisitRequest("visitName", TypeOfVisit.Unscheduled, dueDate);
        assertEquals(dueDate, visitRequest.getAppointmentDueDate());
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingTypeOfVisit() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        CreateVisitRequest visitRequest = createVisitRequestBuilder.adHocVisitRequest("visitName", TypeOfVisit.Unscheduled, dueDate);
        assertEquals(TypeOfVisit.Unscheduled.toString(), visitRequest.getTypeOfVisit());
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingReminderConfiguration() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        CreateVisitRequest visitRequest = createVisitRequestBuilder.adHocVisitRequest("visitName", TypeOfVisit.Unscheduled, dueDate);
        assertNotNull(visitRequest.getAppointmentReminderConfiguration());
    }

    @Test
    public void shouldCreateScheduledVisitRequestGivenWeekOffSet() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        CreateVisitRequest visitRequest = createVisitRequestBuilder.scheduledVisitRequest("visitName", 1);
        assertEquals("visitname", visitRequest.getVisitName());
        assertEquals(dueDate.toLocalDate(), visitRequest.getAppointmentDueDate().toLocalDate());
        assertEquals(TypeOfVisit.Scheduled.toString(), visitRequest.getTypeOfVisit());
        assertEquals(1, visitRequest.getData().get(ClinicVisit.WEEK_NUMBER));
        assertNotNull(visitRequest.getAppointmentReminderConfiguration());
    }

    @Test
    public void shouldCreateBaselineVisitRequestGivenWeekOffSet() {
        CreateVisitRequest visitRequest = createVisitRequestBuilder.baselineVisitRequest();
        assertEquals(TypeOfVisit.Baseline.toString(), visitRequest.getTypeOfVisit());
    }
}
