package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MessageForMedicines implements ITreeCommand {
    public static final String KEY = "0";
    private Patients patients;
    private Clinics clinics;
    private PillReminderService service;

    @Autowired
    public MessageForMedicines(Patients patients, Clinics clinics, PillReminderService service) {
        this.patients = patients;
        this.clinics = clinics;
        this.service = service;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        ArrayList<String> messages = new ArrayList<String>();

        Patient patient = patients.get(ivrContext.ivrSession().getPatientId());
        Clinic clinic = clinics.get(patient.getClinic_id());

        messages.add(clinic.getName());
        messages.add(IVRMessage.ITS_TIME_FOR_THE_PILL);
        for (String medicine : getMedicines(ivrContext.ivrRequest())) {
            messages.add(medicine);
        }
        messages.add(IVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }

    private List<String> getMedicines(IVRRequest ivrRequest) {
        Map<String, String> params = ivrRequest.getTamaParams();
        String regimen = params.get(PillReminderCall.REGIMEN_ID);
        String dosage = params.get(PillReminderCall.DOSAGE_ID);
        return service.medicinesFor(regimen, dosage);
    }
}
