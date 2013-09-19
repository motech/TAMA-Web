package org.motechproject.tama.facility.service;


import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ClinicService {

    private AllClinics allClinics;

    @Autowired
    public ClinicService(AllClinics allClinics) {
        this.allClinics = allClinics;
    }

    public List<Clinic> getAllClinics() {
        return allClinics.getAll();
    }
}
