package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.contract.RescheduleAppointmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RescheduleAppointmentRequestBuilder {

    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Autowired
    public RescheduleAppointmentRequestBuilder(ReminderConfigurationBuilder reminderConfigurationBuilder) {
        this.reminderConfigurationBuilder = reminderConfigurationBuilder;
    }

    public RescheduleAppointmentRequest create(String patientDocId, String clinicVisitId, LocalDate adjustedDueDate) {
        return new RescheduleAppointmentRequest().setExternalId(patientDocId).
                setVisitName(clinicVisitId).
                setAppointmentDueDate(DateUtil.newDateTime(adjustedDueDate)).
                addAppointmentReminderConfiguration(reminderConfigurationBuilder.newAppointmentReminder());
    }
}
