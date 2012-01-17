package org.motechproject.tama.ivr.service;

import org.motechproject.tama.patient.domain.Patient;

public interface AdherenceServiceStrategy {
    public boolean wasAnyDoseMissedLastWeek(Patient patient);
    public boolean wasAnyDoseTakenLateLastWeek(Patient patient);
}
