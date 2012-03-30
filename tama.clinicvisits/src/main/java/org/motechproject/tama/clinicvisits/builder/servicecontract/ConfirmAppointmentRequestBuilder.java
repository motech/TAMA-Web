package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.service.contract.ConfirmAppointmentRequest;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmAppointmentRequestBuilder {

    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Autowired
    public ConfirmAppointmentRequestBuilder(ReminderConfigurationBuilder reminderConfigurationBuilder) {
        this.reminderConfigurationBuilder = reminderConfigurationBuilder;
    }

    public ConfirmAppointmentRequest confirmAppointmentRequest(String patientDocId, String clinicVisitId, DateTime confirmedAppointmentDate) {
        ReminderConfiguration visitReminderConfiguration = reminderConfigurationBuilder.newVisitReminder();
        return new ConfirmAppointmentRequest().setAppointmentConfirmDate(confirmedAppointmentDate)
                                              .setExternalId(patientDocId)
                                              .setVisitName(clinicVisitId)
                                              .setVisitReminderConfiguration(visitReminderConfiguration);
    }
}
