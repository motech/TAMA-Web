package org.motechproject.tama.migration.repository;

import junit.framework.Assert;
import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationMigrationContext.xml")
public class PagedClinicsRepositoryIT extends SpringIntegrationTest {

    @Autowired
    @Qualifier("tamaDbConnector")
    CouchDbConnector tamaCouchDbConnector;

    @Autowired
    AllCities allCities;

    @Autowired
    AllCitiesCache allCitiesCache;

    @Autowired
    PagedClinicsRepository clinicsRepository;

    City city;

    @Override
    public CouchDbConnector getDBConnector() {
        return tamaCouchDbConnector;
    }

    @Before
    public void setup() {
        city = City.newCity("agra");
        allCities.add(city);
        allCitiesCache.refresh();
        markForDeletion(city);
    }

    @Test
    public void shouldLoadPatientEventLogs() {
        List<String> clinicNames = asList("clinic1", "clinic2");

        List<Clinic> clinics = asList(
                ClinicBuilder.startRecording().withDefaults().withName(clinicNames.get(0)).withCity(city).build(),
                ClinicBuilder.startRecording().withDefaults().withName(clinicNames.get(1)).withCity(city).build()
        );

        clinicsRepository.add(clinics.get(0), "userName", false);
        clinicsRepository.add(clinics.get(1), "userName", false);
        markForDeletion(clinics);

        assertTrue(clinicNames.contains(clinicsRepository.get(0, 1).get(0).getName()));
        assertTrue(clinicNames.contains(clinicsRepository.get(1, 1).get(0).getName()));
        assertTrue(clinicsRepository.get(2, 1).isEmpty());
        assertClinicDependencies(clinicsRepository.get(0, 1));
    }

    private void assertClinicDependencies(List<Clinic> clinics) {
        for (Clinic clinic : clinics) {
            Assert.assertNotNull(clinic.getCity());
        }
    }
}
