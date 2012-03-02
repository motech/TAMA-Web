package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class AppointmentCalendarRequestBuilder {

    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";

    private Properties appointmentsProperties;
    private CreateVisitRequestBuilder createVisitRequestBuilder;

    @Autowired
    public AppointmentCalendarRequestBuilder(CreateVisitRequestBuilder createVisitRequestBuilder, @Qualifier("appointments") Properties appointmentsProperties) {
        this.appointmentsProperties = appointmentsProperties;
        this.createVisitRequestBuilder = createVisitRequestBuilder;
    }

    public AppointmentCalendarRequest calendarForPatient(String patientDocId) {
        AppointmentCalendarRequest appointmentCalendarRequest = new AppointmentCalendarRequest().setExternalId(patientDocId);
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsProperties.getProperty(APPOINTMENT_SCHEDULE));

        appointmentCalendarRequest.addVisitRequest(createVisitRequestBuilder.baselineVisitRequest());
        for (Integer appointmentWeek : appointmentWeeks) {
            CreateVisitRequest scheduledVisitRequest = createVisitRequestBuilder.scheduledVisitRequest("week" + appointmentWeek, appointmentWeek);
            appointmentCalendarRequest.addVisitRequest(scheduledVisitRequest);
        }
        return appointmentCalendarRequest;
    }
}
