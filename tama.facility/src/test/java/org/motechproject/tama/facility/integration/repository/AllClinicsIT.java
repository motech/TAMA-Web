package org.motechproject.tama.facility.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.facility.repository.AllMonitoringAgents;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class AllClinicsIT extends SpringIntegrationTest {

    @Autowired
    private AllClinics allClinics;
    @Autowired
    private AllCities allCities;
    @Autowired
    private AllCitiesCache allCitiesCache;
    @Autowired
    private AllMonitoringAgents allMonitoringAgents;

    @Before
    public void before() {
        super.before();
        markForDeletion(allClinics.getAll().toArray());
        markForDeletion(allCities.getAll().toArray());
        markForDeletion(allMonitoringAgents.getAll().toArray());
        deleteAll();
    }

    @After
    public void after() {
        markForDeletion(allClinics.getAll().toArray());
        markForDeletion(allCities.getAll().toArray());
        markForDeletion(allMonitoringAgents.getAll().toArray());
        super.after();
    }

    @Test
    public void shouldFindClinicianContactByPhoneNumber() {
        City city = City.newCity("Pune");
        allCities.add(city);
        
        MonitoringAgent monitoringAgent = MonitoringAgent.newMonitoringAgent("testMonitoringAgent");
        allMonitoringAgents.add(monitoringAgent);

        allCitiesCache.refresh();

        Clinic.ClinicianContact contact = new Clinic.ClinicianContact("name", "1234");

        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withMonitoringAgent(monitoringAgent).withClinicianContacts(contact).build();
        allClinics.add(clinic, "user");
        markForDeletion(clinic);

        assertNotNull(allClinics.findByPhoneNumber("1234"));
    }

    @Test
    public void shouldLoadCorrespondingCityWhenQueryingClinic() {
        City city = City.newCity("Pune");
        allCities.add(city);

        allCitiesCache.refresh();

        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        allClinics.add(clinic, "admin");

        Clinic returnedClinic = allClinics.get(clinic.getId());

        assertNotNull(returnedClinic);
        assertNotNull(returnedClinic.getCity());
        Assert.assertEquals(city.getName(), returnedClinic.getCity().getName());

        markForDeletion(clinic);
        markForDeletion(city);
    }

    @Test
    public void getAllShouldLoadCorrespondingCities() {
        City city = City.newCity("Pune");
        allCities.add(city);

        City anotherCity = City.newCity("Chennai");
        allCities.add(anotherCity);

        allCitiesCache.refresh();

        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        allClinics.add(clinic, "admin");

        Clinic anotherClinic = ClinicBuilder.startRecording().withCity(anotherCity).build();
        allClinics.add(anotherClinic, "admin");

        List<Clinic> returnedClinics = allClinics.getAll();

        Assert.assertEquals(city.getName(), returnedClinics.get(0).getCity().getName());
        Assert.assertEquals(anotherCity.getName(), returnedClinics.get(1).getCity().getName());

        markForDeletion(anotherClinic);
        markForDeletion(anotherCity);

        markForDeletion(clinic);
        markForDeletion(city);
    }
}
