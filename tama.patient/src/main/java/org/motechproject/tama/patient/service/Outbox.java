package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.Patient;

public interface Outbox {

    public void enroll(Patient patient);

    public void reEnroll(Patient dbPatient, Patient patient);

    public String addMessage(String patientId, String voiceMessageTypeName);
}