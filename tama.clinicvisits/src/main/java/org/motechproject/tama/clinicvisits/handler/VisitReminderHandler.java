package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.clinicvisits.service.VisitReminderService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitReminderHandler {

    private AllPatients allPatients;
    private AllClinicVisits allClinicVisits;
    private VisitReminderService visitReminderService;

    @Autowired
    public VisitReminderHandler(AllPatients allPatients, AllClinicVisits allClinicVisits, VisitReminderService visitReminderService) {
        this.allPatients = allPatients;
        this.allClinicVisits = allClinicVisits;
        this.visitReminderService = visitReminderService;
    }

    @MotechListener(subjects = EventKeys.VISIT_REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent visitReminderEvent) {
        String patientDocId = visitReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String visitName = visitReminderEvent.getParameters().get(EventKeys.VISIT_NAME).toString();

        Patient patient = allPatients.get(patientDocId);
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, visitName);

        visitReminderService.addOutboxMessage(patient, clinicVisit);

        visitReminderService.raiseAlert(patient, clinicVisit);
    }
}
