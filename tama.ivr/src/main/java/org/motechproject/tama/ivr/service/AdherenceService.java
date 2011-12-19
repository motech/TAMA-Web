package org.motechproject.tama.ivr.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdherenceService {

    private Map<CallPreference, AdherenceServiceStratergy> adherenceServices = new HashMap<CallPreference, AdherenceServiceStratergy>();

    public void register(CallPreference callPreference, AdherenceServiceStratergy adherenceServiceStratergy) {
        adherenceServices.put(callPreference, adherenceServiceStratergy);
    }

    public boolean isDosageMissedLastWeek(Patient patient) {
        AdherenceServiceStratergy adherenceServiceStratergy = adherenceServices.get(patient.getPatientPreferences().getCallPreference());
        return adherenceServiceStratergy.isDosageMissedLastWeek(patient);
    }

    public boolean anyDoseTakenLateSince(Patient patient, LocalDate since) {
        AdherenceServiceStratergy adherenceServiceStratergy = adherenceServices.get(patient.getPatientPreferences().getCallPreference());
        return adherenceServiceStratergy.anyDoseTakenLateSince(patient, since);
    }
}
