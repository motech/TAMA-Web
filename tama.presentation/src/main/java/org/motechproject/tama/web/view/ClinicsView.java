package org.motechproject.tama.web.view;


import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.repository.AllClinics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClinicsView {

    private final AllClinics allClinics;

    public ClinicsView(AllClinics allClinics){
        this.allClinics = allClinics;
    }

    public List<Clinic> getAll() {
        List<Clinic> allClinics = this.allClinics.getAll();
        Collections.sort(allClinics, new Comparator<Clinic>() {
            @Override
            public int compare(Clinic clinic, Clinic otherClinic) {
                return clinic.getName().toLowerCase().compareTo(otherClinic.getName().toLowerCase());
            }
        });
        return allClinics;
    }
}
