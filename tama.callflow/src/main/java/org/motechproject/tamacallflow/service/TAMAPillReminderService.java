package org.motechproject.tamacallflow.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.domain.TAMAPillRegimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TAMAPillReminderService {

    private PillReminderService pillReminderService;

    @Autowired
    public TAMAPillReminderService(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    public TAMAPillRegimen getPillRegimen(String patientId) {
        return new TAMAPillRegimen(pillReminderService.getPillRegimen(patientId));
    }
}