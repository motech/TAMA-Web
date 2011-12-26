package org.motechproject.tama.ivr.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdherenceService {

    private Map<CallPreference, AdherenceServiceStrategy> adherenceServices = new HashMap<CallPreference, AdherenceServiceStrategy>();

    public void register(CallPreference callPreference, AdherenceServiceStrategy adherenceServiceStrategy) {
        adherenceServices.put(callPreference, adherenceServiceStrategy);
    }

    public boolean isDosageMissedLastWeek(Patient patient) {
        AdherenceServiceStrategy adherenceServiceStrategy = adherenceServices.get(patient.callPreference());
        return adherenceServiceStrategy.wasAnyDoseMissedLastWeek(patient);
    }

    public boolean anyDoseTakenLateSince(Patient patient, LocalDate since) {
        AdherenceServiceStrategy adherenceServiceStrategy = adherenceServices.get(patient.callPreference());
        return adherenceServiceStrategy.wasAnyDoseTakenLateSince(patient, since);
    }

    public AdherenceComplianceReport lastWeekAdherence(Patient patient) {
        LocalDate lastSevenDays = DateUtil.today().minusDays(6);
        boolean anyDoseTakenLateLastWeek = anyDoseTakenLateSince(patient, lastSevenDays);
        boolean dosageMissedLastWeek = isDosageMissedLastWeek(patient);
        return new AdherenceComplianceReport(anyDoseTakenLateLastWeek, dosageMissedLastWeek);
    }
}
