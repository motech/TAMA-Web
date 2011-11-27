package org.motechproject.tama.service;

import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageTimeLine;
import org.motechproject.tama.domain.TAMAPillRegimen;
import org.motechproject.tama.ivr.DosageResponseWithDate;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.web.view.SuspendedAdherenceData;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DosageAdherenceService {

    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    private TAMAPillReminderService pillReminderService;

    @Autowired
    public DosageAdherenceService(AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
    }

    public void recordAdherence(SuspendedAdherenceData suspendedAdherenceData) {
        TAMAPillRegimen pillRegimen = pillReminderService.getPillRegimen(suspendedAdherenceData.patientId());
        DosageTimeLine dosageTimeLine = pillRegimen.getDosageTimeLine(suspendedAdherenceData.suspendedFrom(), DateUtil.now());
        while (dosageTimeLine.hasNext()) {
            DosageResponseWithDate dosage = dosageTimeLine.next();
            DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog(suspendedAdherenceData.patientId(), pillRegimen.getId(), dosage.getDosageId(), suspendedAdherenceData.getAdherenceDataWhenPatientWasSuspended().getStatus(), dosage.getDosageDate());
            allDosageAdherenceLogs.add(dosageAdherenceLog);
        }
    }
}