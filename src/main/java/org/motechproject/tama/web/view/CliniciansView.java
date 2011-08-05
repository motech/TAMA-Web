package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CliniciansView {

    private final Clinicians clinicians;

    public CliniciansView(Clinicians clinicians){
        this.clinicians = clinicians;
    }

    public List<Clinician> getAll() {
        List<Clinician> allClinicians = clinicians.getAll();
        Collections.sort(allClinicians, new Comparator<Clinician>() {
            @Override
            public int compare(Clinician clinician, Clinician otherClinician) {
                return clinician.getName().toLowerCase().compareTo(otherClinician.getName().toLowerCase());
            }
        });
        return allClinicians;
    }
}
