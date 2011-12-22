package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;

public interface Outbox {

    public void enroll(Patient patient);

    public void reEnroll(Patient patient);
}