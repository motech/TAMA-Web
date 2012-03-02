package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.ConfirmAppointmentRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmAppointmentRequestBuilder {

    private ReminderConfigurationBuilder reminderConfigurationBuilder;

    @Autowired
    public ConfirmAppointmentRequestBuilder(ReminderConfigurationBuilder reminderConfigurationBuilder) {
        this.reminderConfigurationBuilder = reminderConfigurationBuilder;
    }

    public ConfirmAppointmentRequest confirmAppointmentRequest(String patientDocId, String clinicVisitId, DateTime confirmedVisitDate) {
        ReminderConfiguration visitReminderConfiguration = reminderConfigurationBuilder.newVisitReminder();
        return new ConfirmAppointmentRequest().setAppointmentConfirmDate(confirmedVisitDate)
                                              .setExternalId(patientDocId)
                                              .setVisitName(clinicVisitId)
                                              .setVisitReminderConfiguration(visitReminderConfiguration);
    }
}
