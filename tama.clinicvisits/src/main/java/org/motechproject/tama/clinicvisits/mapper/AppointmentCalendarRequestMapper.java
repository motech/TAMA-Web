package org.motechproject.tama.clinicvisits.mapper;

import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class AppointmentCalendarRequestMapper {

    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";

    private Properties appointmentsProperties;
    private VisitRequestMapper visitRequestMapper;

    @Autowired
    public AppointmentCalendarRequestMapper(VisitRequestMapper visitRequestMapper, @Qualifier("appointments") Properties appointmentsProperties) {
        this.appointmentsProperties = appointmentsProperties;
        this.visitRequestMapper = visitRequestMapper;
    }

    public AppointmentCalendarRequest map(String patientDocId) {
        Map<String, VisitRequest> visitRequests = new HashMap<String, VisitRequest>();

        visitRequests.put(ClinicVisit.BASELINE, visitRequestMapper.mapBaselineVisit());
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsProperties.getProperty(APPOINTMENT_SCHEDULE));
        for (Integer appointmentWeek : appointmentWeeks) {
            VisitRequest visitRequest = visitRequestMapper.map(appointmentWeek, TypeOfVisit.Scheduled);
            visitRequests.put("week" + appointmentWeek, visitRequest);
        }
        return new AppointmentCalendarRequest()
                .setExternalId(patientDocId)
                .setVisitRequests(visitRequests);
    }
}
