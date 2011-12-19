package org.motechproject.tama.patient.service;

import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllUniquePatientFields allUniquePatientFields;

    @Autowired
    public PatientService(AllPatients allPatients, AllUniquePatientFields allUniquePatientFields) {
        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
    }

    public void update(Patient patient) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        patient.setRegistrationDate(dbPatient.getRegistrationDate());

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
    }

    public void suspend(String patientId) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        update(patient);
    }
}






