package org.motechproject.tamacallflow.service;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TAMAPillReminderService {

    private PillReminderService pillReminderService;

    @Autowired
    public TAMAPillReminderService(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    public PillRegimen getPillRegimen(String patientId) {
        return new PillRegimen(pillReminderService.getPillRegimen(patientId));
    }

    public void setLastCapturedDate(String pillRegimenId, String dosageId, LocalDate lastCapturedDate) {
        pillReminderService.dosageStatusKnown(pillRegimenId, dosageId, lastCapturedDate);
    }
}