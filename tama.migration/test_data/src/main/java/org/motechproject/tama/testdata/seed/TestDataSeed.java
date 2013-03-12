package org.motechproject.tama.testdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataSeed {

    public static final String TEST_SEED = "testseed";

    private AllCities allCities;
    private AllCitiesCache citiesCache;
    private AllClinics allClinics;
    private AllClinicians allClinicians;

    @Autowired
    public TestDataSeed(AllCities allCities,
                        AllCitiesCache citiesCache,
                        AllClinics allClinics,
                        AllClinicians allClinicians) {

        this.allCities = allCities;
        this.citiesCache = citiesCache;
        this.allClinics = allClinics;
        this.allClinicians = allClinicians;
    }

    @Seed(version = "3.0", priority = 1, test = true)
    public void loadTesData() {
        City city = allCities.getAll().get(0);
        Clinic clinic1 = createClinic(city);
        Clinic clinic2 = createClinic(city);
        createClinician(clinic1, "drpujari", "9999999999", "drpujari");
        createClinician(clinic2, "saple", "9999999999", "saple");
    }

    private Clinician createClinician(Clinic clinic, String name, String phoneNumber, String userName) {
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).withName(name).withContactNumber(phoneNumber).withUserName(userName).withPassword("password").build();
        allClinicians.add(clinician, TEST_SEED);
        return clinician;
    }

    private Clinic createClinic(City city) {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        allClinics.add(clinic, TEST_SEED);
        return clinic;
    }
}
