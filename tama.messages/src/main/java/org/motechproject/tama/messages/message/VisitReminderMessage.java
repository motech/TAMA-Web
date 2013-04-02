package org.motechproject.tama.messages.message;


import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.timeconstruct.TimeConstructBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;

import java.util.ArrayList;
import java.util.List;

public class VisitReminderMessage {

    private Integer remindFrom;
    private ClinicVisit clinicVisit;
    private Patient patient;

    public VisitReminderMessage(Integer remindFrom, ClinicVisit clinicVisit, Patient patient) {
        this.remindFrom = remindFrom;
        this.clinicVisit = clinicVisit;
        this.patient = patient;
    }

    public boolean isValid(DateTime reference) {
        if (null != clinicVisit) {
            DateTime confirmed = clinicVisit.getConfirmedAppointmentDate();
            DateTime remindedFrom = confirmed.minusDays(remindFrom);
            return !reference.isBefore(remindedFrom) && !reference.isAfter(confirmed) && clinicVisit.isUpcoming();
        } else {
            return false;
        }
    }

    public String getId() {
        return patient.getId() + clinicVisit.getId();
    }

    public KookooIVRResponseBuilder build(TAMAIVRContext context) {
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder().withSid(context.callId());
        List<String> message = constructMessage(clinicVisit.getConfirmedAppointmentDate(), patient);
        return ivrResponseBuilder.withPlayAudios(message.toArray(new String[0]));
    }

    private List<String> constructMessage(DateTime confirmedAppointmentDate, Patient patient) {
        List<String> message = new ArrayList<>();
        message.add(TamaIVRMessage.NEXT_VISIT_REMINDER_IS_DUE_PART1);
        message.add(buildDayOfWeekMessage(confirmedAppointmentDate));
        message.add(TamaIVRMessage.NEXT_VISIT_REMINDER_IS_DUE_PART2);
        message.add(buildDayMessage(confirmedAppointmentDate));
        message.add(buildMonthMessage(confirmedAppointmentDate));
        message.add(TamaIVRMessage.NEXT_VISIT_REMINDER_IS_DUE_PART3);
        message.addAll(buildTimeMessage(patient, confirmedAppointmentDate));
        message.add(TamaIVRMessage.NEXT_VISIT_REMINDER_IS_DUE_PART4);
        return message;
    }

    private List<String> buildTimeMessage(Patient patient, DateTime confirmedAppointmentDate) {
        String language = patient.getPatientPreferences().getIvrLanguage().getCode();
        return new TimeConstructBuilder().builder(language).build(confirmedAppointmentDate.toLocalTime());
    }

    private String buildMonthMessage(DateTime confirmedAppointmentDate) {
        return TamaIVRMessage.getMonthOfYearFile(confirmedAppointmentDate.monthOfYear().getAsText());
    }

    private String buildDayMessage(DateTime confirmedAppointmentDate) {
        return new TamaIVRMessage.DateMessage(confirmedAppointmentDate.getDayOfMonth()).value();
    }

    private String buildDayOfWeekMessage(DateTime confirmedAppointmentDate) {
        return TamaIVRMessage.getDayOfWeekFile(confirmedAppointmentDate.dayOfWeek().getAsText());
    }
}
