package org.motechproject.tama.ivr.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.Patient;

public interface AdherenceServiceStratergy {
    public boolean isDosageMissedLastWeek(Patient patient);
    public boolean anyDoseTakenLateSince(Patient patient, LocalDate since);
}
