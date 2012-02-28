package org.motechproject.tama.clinicvisits.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class VisitRequestBuilderTest {

    private VisitRequestBuilder visitRequestBuilder;
    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    public VisitRequestBuilderTest() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FROM, "10");
        reminderConfigurationBuilder = new ReminderConfigurationBuilder(appointmentsProperties);
        visitRequestBuilder = new VisitRequestBuilder(reminderConfigurationBuilder);
    }

    @Test
    public void shouldCreateVisitRequestGivenDueDate() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithoutReminderRequest(dueDate, TypeOfVisit.Unscheduled);
        assertEquals(dueDate, visitRequest.getDueDate());
        assertEquals(TypeOfVisit.Unscheduled, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingDueDate() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithReminderRequest(dueDate, TypeOfVisit.Unscheduled);
        assertEquals(dueDate, visitRequest.getDueDate());
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingTypeOfVisit() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithReminderRequest(dueDate, TypeOfVisit.Unscheduled);
        assertEquals(TypeOfVisit.Unscheduled, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
    }

    @Test
    public void shouldBuildVisitWithReminderRequestHavingReminderConfiguration() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithReminderRequest(dueDate, TypeOfVisit.Unscheduled);
        assertNotNull(visitRequest.getReminderConfiguration());
    }

    @Test
    public void shouldCreateScheduledVisitRequestGivenWeekOffSet() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.scheduledVisitRequest(1);
        assertEquals(dueDate.toLocalDate(), visitRequest.getDueDate().toLocalDate());
        assertEquals(TypeOfVisit.Scheduled, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
        assertEquals(1, visitRequest.getData().get(ClinicVisit.WEEK_NUMBER));
        assertNotNull(visitRequest.getReminderConfiguration());
    }

    @Test
    public void shouldCreateBaselineVisitRequestGivenWeekOffSet() {
        VisitRequest visitRequest = visitRequestBuilder.baselineVisitRequest();
        assertEquals(TypeOfVisit.Baseline, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
    }
}
