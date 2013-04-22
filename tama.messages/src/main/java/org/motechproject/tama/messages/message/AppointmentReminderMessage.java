package org.motechproject.tama.messages.message;


import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;

import java.util.List;

public class AppointmentReminderMessage {

    private Integer remindFrom;
    private Appointment appointment;
    private Patient patient;

    public AppointmentReminderMessage(Integer remindFrom, Appointment appointment, Patient patient) {
        this.remindFrom = remindFrom;
        this.appointment = appointment;
        this.patient = patient;
    }

    public boolean isValid(LocalDate reference) {
        if (null != appointment) {
            LocalDate dueDate = appointment.getDueDate();
            LocalDate remindedFrom = dueDate.minusDays(remindFrom);
            return (!reference.isBefore(remindedFrom) && !reference.isAfter(dueDate) && appointment.isUpcoming());
        } else {
            return false;
        }
    }

    public String getId() {
         return patient.getId() + appointment.getDueDate();
    }

    public KookooIVRResponseBuilder build(TAMAIVRContext context) {
        String clinicPhoneNumber = patient.getClinic().getPhone();
        List<String> allNumberFileNames = TamaIVRMessage.getAllNumberFileNames("0" + clinicPhoneNumber);
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder().language(context.preferredLanguage()).withSid(context.callId());

        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NEXT_CLINIC_VISIT_IS_DUE_PART1);
        ivrResponseBuilder.withPlayAudios(allNumberFileNames.toArray(new String[allNumberFileNames.size()]));
        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NEXT_CLINIC_VISIT_IS_DUE_PART2);
        return ivrResponseBuilder;
    }
}
