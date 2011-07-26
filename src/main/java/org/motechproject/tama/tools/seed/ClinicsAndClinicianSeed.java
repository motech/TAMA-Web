package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicsAndClinicianSeed extends Seed{
    @Autowired
    private Clinics clinics;
    @Autowired
    private Clinicians clinicians;

    @Override
    protected void load() {
        Clinic clinic = new Clinic("clinicId");
        clinic.setCity(City.newCity("Bangalore"));
        clinic.setName("clinic");
        clinic.setPhone("9898989898");
        clinics.add(clinic);

        Clinician clinician = new Clinician();
        clinician.setClinic(clinic);
        clinician.setContactNumber("9494949494");
        clinician.setAlternateContactNumber("9595959595");
        clinician.setName("nurse");
        clinician.setUsername("nurse");
        clinician.setPassword("nurse");
        clinicians.add(clinician);
    }
}
