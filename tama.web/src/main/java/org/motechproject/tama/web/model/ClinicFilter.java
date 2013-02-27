package org.motechproject.tama.web.model;

import lombok.EqualsAndHashCode;
import org.motechproject.tama.facility.domain.Clinic;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@EqualsAndHashCode
public class ClinicFilter {

    private List<Clinic> clinics;
    private String clinicId;

    public ClinicFilter(List<Clinic> clinics) {
        Clinic defaultClinic = defaultClinicOption();
        this.clinics = new ArrayList<>(asList(defaultClinic));
        this.clinics.addAll(clinics);
    }

    private Clinic defaultClinicOption() {
        Clinic defaultClinic = Clinic.newClinic();
        defaultClinic.setName("");
        return defaultClinic;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public List<Clinic> getAllClinics() {
        return clinics;
    }
}
