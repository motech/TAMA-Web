package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateVisitRequestBuilder {

    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Autowired
    public CreateVisitRequestBuilder(ReminderConfigurationBuilder reminderConfigurationBuilder) {
        this.reminderConfigurationBuilder = reminderConfigurationBuilder;
    }

    public CreateVisitRequest baselineVisitRequest() {
        return new CreateVisitRequest().setVisitName(TypeOfVisit.Baseline.toLowerCase())
                                       .setTypeOfVisit(TypeOfVisit.Baseline.toString());
    }

    public CreateVisitRequest scheduledVisitRequest(String visitName, Integer weekOffset) {
        DateTime dueDate = DateUtil.newDateTime(DateUtil.today().plusWeeks(weekOffset));
        return new CreateVisitRequest().setVisitName(visitName.toLowerCase())
                                       .setTypeOfVisit(TypeOfVisit.Scheduled.toString())
                                       .setAppointmentDueDate(dueDate)
                                       .setAppointmentReminderConfiguration(reminderConfigurationBuilder.newAppointmentReminder())
                                       .addData(ClinicVisit.WEEK_NUMBER, weekOffset);
    }

    public CreateVisitRequest adHocVisitRequest(String visitName, TypeOfVisit typeOfVisit, DateTime dueDate) {
        return new CreateVisitRequest().setVisitName(visitName.toLowerCase())
                                       .setTypeOfVisit(typeOfVisit.toString())
                                       .setAppointmentDueDate(dueDate)
                                       .setAppointmentReminderConfiguration(reminderConfigurationBuilder.newAppointmentReminder());
    }

    public CreateVisitRequest adHocVisitRequestForToday(String visitName, TypeOfVisit typeOfVisit, DateTime dueDate) {
        return new CreateVisitRequest().setVisitName(visitName.toLowerCase())
                                       .setTypeOfVisit(typeOfVisit.toString())
                                       .setAppointmentDueDate(dueDate);
    }
}
