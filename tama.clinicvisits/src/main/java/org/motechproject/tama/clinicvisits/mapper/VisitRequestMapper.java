package org.motechproject.tama.clinicvisits.mapper;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitRequestMapper {

    private ReminderConfigurationMapper reminderConfigurationMapper;

    @Autowired
    public VisitRequestMapper(ReminderConfigurationMapper reminderConfigurationMapper) {
        this.reminderConfigurationMapper = reminderConfigurationMapper;
    }

    public VisitRequest map(DateTime dueDate, TypeOfVisit typeOfVisit) {
        return new VisitRequest().setDueDate(dueDate).addData(ClinicVisit.TYPE_OF_VISIT, typeOfVisit);
    }

    public VisitRequest mapScheduledVisit(Integer weekOffset) {
        DateTime dueDate = DateUtil.now().plusWeeks(weekOffset);
        return new VisitRequest()
                .setDueDate(dueDate)
                .setReminderConfiguration(reminderConfigurationMapper.map())
                .addData(ClinicVisit.WEEK_NUMBER, weekOffset).addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Scheduled);
    }

    public VisitRequest mapBaselineVisit() {
        return new VisitRequest().addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Baseline);
    }

}
