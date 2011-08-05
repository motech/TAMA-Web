package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.Clinics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClinicsView {

    private final Clinics clinics;

    public ClinicsView(Clinics clinics){
        this.clinics = clinics;
    }

    public List<Clinic> getAll() {
        List<Clinic> allClinics = clinics.getAll();
        Collections.sort(allClinics, new Comparator<Clinic>() {
            @Override
            public int compare(Clinic clinic, Clinic otherClinic) {
                return clinic.getName().toLowerCase().compareTo(otherClinic.getName().toLowerCase());
            }
        });
        return allClinics;
    }
}
