package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageForMedicines extends BaseTreeCommand {
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
        for (String medicine : getMedicines(ivrContext)) {
            messages.add(medicine);
        }
        messages.add(IVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }

    private List<String> getMedicines(IVRContext ivrContext) {
        return service.medicinesFor(getRegimenIdFrom(ivrContext), getDosageIdFrom(ivrContext));
    }
}
