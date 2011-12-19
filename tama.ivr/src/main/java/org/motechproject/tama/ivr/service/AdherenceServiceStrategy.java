package org.motechproject.tama.ivr.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.Patient;

public interface AdherenceServiceStrategy {
    public boolean wasAnyDoseMissedLastWeek(Patient patient);
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since);
}
