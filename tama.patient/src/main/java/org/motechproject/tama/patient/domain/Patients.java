package org.motechproject.tama.patient.domain;

import java.util.HashMap;
import java.util.List;

public class Patients {

    private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();

    public Patients() {
    }

    public Patients(List<Patient> patients) {
        for (Patient patient : patients) {
            patientMap.put(patient.getId(), patient);
        }
    }

    public Patient getBy(String id) {
        return patientMap.get(id);
    }

    public boolean isEmpty() {
        return patientMap.isEmpty();
    }
}