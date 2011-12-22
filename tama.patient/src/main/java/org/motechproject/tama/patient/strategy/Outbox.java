package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;

public abstract class Outbox {

    public abstract void enroll(Patient patient);

    public abstract void reEnroll(Patient patient);
}