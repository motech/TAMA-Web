package org.motechproject.tama.ivr.service;

import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdherenceService {

    private Map<CallPreference, AdherenceServiceStrategy> adherenceServices = new HashMap<CallPreference, AdherenceServiceStrategy>();

    public void register(CallPreference callPreference, AdherenceServiceStrategy adherenceServiceStrategy) {
        adherenceServices.put(callPreference, adherenceServiceStrategy);
    }

    public AdherenceComplianceReport lastWeekAdherence(Patient patient) {
        AdherenceServiceStrategy adherenceServiceStrategy = adherenceServices.get(patient.callPreference());

        boolean anyDoseTakenLateLastWeek = adherenceServiceStrategy.wasAnyDoseTakenLateLastWeek(patient);
        boolean dosageMissedLastWeek = adherenceServiceStrategy.wasAnyDoseMissedLastWeek(patient);

        return new AdherenceComplianceReport(anyDoseTakenLateLastWeek, dosageMissedLastWeek);
    }
}
