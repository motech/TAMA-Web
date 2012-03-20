package org.motechproject.tama.patient.domain;

import java.util.ArrayList;
import java.util.Collection;

public class Patients extends ArrayList<Patient> {
    public Patients() {
    }

    public Patients(Collection<? extends Patient> c) {
        super(c);
    }

    public Patient getBy(String id) {
        for (Patient patient : this) {
            if (patient.getId().equals(id))
                return patient;
        }
        return null;
    }
}