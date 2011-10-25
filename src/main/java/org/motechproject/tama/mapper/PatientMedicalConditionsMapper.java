package org.motechproject.tama.mapper;

import org.motechproject.tama.domain.LabResults;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientMedicalConditions;
import org.motechproject.tama.domain.Regimen;

public class PatientMedicalConditionsMapper {
    private Patient patient;
    private LabResults labResults;
    private Regimen regimen;

    public PatientMedicalConditionsMapper(Patient patient, LabResults labResults, Regimen regimen) {
        this.patient = patient;
        this.labResults = labResults;
        this.regimen = regimen;
    }

    public PatientMedicalConditions map() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();

        patientMedicalConditions.setRegimenName(regimen.getName());
        patientMedicalConditions.setGender(patient.getGender().getType());
        patientMedicalConditions.setAge(patient.getAge());
        patientMedicalConditions.setCd4Count(labResults.latestCD4Count());

        return patientMedicalConditions;
    }
}
