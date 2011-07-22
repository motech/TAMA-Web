package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminderAction {
    private Patients patients;

    private IVROutgoingCall outgoingCall;

    @Autowired
    public PillReminderAction(IVROutgoingCall outgoingCall, Patients patients) {
        this.outgoingCall = outgoingCall;
        this.patients = patients;
    }

    public void execute(String patientDocId) {
        Patient patient = patients.get(patientDocId);
        if (patient == null || patient.isNotActive()) return;
        outgoingCall.makeCall(patient.getIVRMobilePhoneNumber());
    }

}
