package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.UniquePatientField;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllUniquePatientFields allUniquePatientFields;
    private PillReminderService pillReminderService;
    private PatientCallServices patientCallServices;
    private AllTreatmentAdvices allTreatmentAdvices;
    private TAMASchedulerService tamaSchedulerService;

    @Autowired
    public PatientService(AllPatients allPatients, AllUniquePatientFields allUniquePatientFields, TAMASchedulerService tamaSchedulerService, AllTreatmentAdvices allTreatmentAdvices, PillReminderService pillReminderService) {
        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.pillReminderService = pillReminderService;
        this.patientCallServices = new PatientCallServices(tamaSchedulerService, pillReminderService, allTreatmentAdvices);
    }

    public void create(Patient patient, String clinicName) {
        allPatients.addToClinic(patient, clinicName);
        this.patientCallServices.patientCreated(patient);
    }

    public void update(Patient patient) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        List<UniquePatientField> oldUniquePatientFields = allUniquePatientFields.get(patient);
        allUniquePatientFields.remove(patient);
        try {
            allUniquePatientFields.add(patient);
        } catch (TamaException e) {
            for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                allUniquePatientFields.add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
            }
            throw e;
        }
        allPatients.update(patient);
        this.patientCallServices.patientUpdated(dbPatient, patient);
    }
}
