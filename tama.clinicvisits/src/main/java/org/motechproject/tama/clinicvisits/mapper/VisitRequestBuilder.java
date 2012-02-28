package org.motechproject.tama.clinicvisits.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitRequestBuilder {

    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Autowired
    public VisitRequestBuilder(ReminderConfigurationBuilder reminderConfigurationBuilder) {
        this.reminderConfigurationBuilder = reminderConfigurationBuilder;
    }

    public VisitRequest visitWithoutReminderRequest(DateTime dueDate, TypeOfVisit typeOfVisit) {
        return new VisitRequest().setDueDate(dueDate).addData(ClinicVisit.TYPE_OF_VISIT, typeOfVisit);
    }

    public VisitRequest visitWithReminderRequest(DateTime dueDate, TypeOfVisit typeOfVisit) {
        return new VisitRequest()
                .setDueDate(dueDate)
                .setReminderConfiguration(reminderConfigurationBuilder.newDefault())
                .addData(ClinicVisit.TYPE_OF_VISIT, typeOfVisit);
    }

    public VisitRequest scheduledVisitRequest(Integer weekOffset) {
        DateTime dueDate = DateUtil.now().plusWeeks(weekOffset);
        VisitRequest visitRequest = visitWithReminderRequest(dueDate, TypeOfVisit.Scheduled);
        return visitRequest.addData(ClinicVisit.WEEK_NUMBER, weekOffset);
    }

    public VisitRequest baselineVisitRequest() {
        return new VisitRequest().addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Baseline);
    }
}
