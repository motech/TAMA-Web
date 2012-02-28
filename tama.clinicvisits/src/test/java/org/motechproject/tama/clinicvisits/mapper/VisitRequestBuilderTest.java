package org.motechproject.tama.clinicvisits.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
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

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationMapper.REMIND_FROM, "10");
        ReminderConfigurationMapper reminderConfigurationMapper = new ReminderConfigurationMapper(appointmentsProperties);
        visitRequestBuilder = new VisitRequestBuilder(reminderConfigurationMapper);
    }

    @Test
    public void shouldCreateVisitRequestGivenDueDate() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithoutReminder(dueDate, TypeOfVisit.Unscheduled);
        assertEquals(dueDate, visitRequest.getDueDate());
        assertEquals(TypeOfVisit.Unscheduled, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
    }

    @Test
    public void shouldCreateScheduledVisitRequestGivenWeekOffSet() {
        DateTime dueDate = DateUtil.now().plusWeeks(1);
        VisitRequest visitRequest = visitRequestBuilder.visitWithReminder(1);
        assertEquals(dueDate.toLocalDate(), visitRequest.getDueDate().toLocalDate());
        assertEquals(TypeOfVisit.Scheduled, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
        assertEquals(1, visitRequest.getData().get(ClinicVisit.WEEK_NUMBER));
        assertNotNull(visitRequest.getReminderConfiguration());
    }

    @Test
    public void shouldCreateBaselineVisitRequestGivenWeekOffSet() {
        VisitRequest visitRequest = visitRequestBuilder.baselineVisit();
        assertEquals(TypeOfVisit.Baseline, visitRequest.getData().get(ClinicVisit.TYPE_OF_VISIT));
    }
}
