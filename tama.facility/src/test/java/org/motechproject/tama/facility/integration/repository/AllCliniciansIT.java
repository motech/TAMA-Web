package org.motechproject.tama.facility.integration.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.facility.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class AllCliniciansIT extends SpringIntegrationTest {

    @Autowired
    private AllClinicians allClinicians;
    @Autowired
    private AllClinics allClinics;

    @Before
    public void before() {
        super.before();
        markForDeletion(allClinicians.getAll().toArray());
        markForDeletion(allClinics.getAll().toArray());
        deleteAll();
    }

    @After
    public void after() {
        markForDeletion(allClinicians.getAll().toArray());
        markForDeletion(allClinics.getAll().toArray());
        super.after();
    }

    @Test
    public void getShouldLoadClinicWhenQueryingClinician() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        allClinics.add(clinic, "admin");

        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).build();
        allClinicians.add(clinician, "admin");

        Clinician returnedClinician = allClinicians.get(clinician.getId());

        assertNotNull(returnedClinician);
        assertNotNull(returnedClinician.getClinic());
        assertEquals(clinic.getName(), returnedClinician.getClinic().getName());

        markForDeletion(clinic);
        markForDeletion(clinician);
    }

    @Test
    public void getAllShouldLoadClinicsWhenQueryingClinicians() {
        Clinic clinic1 = ClinicBuilder.startRecording().withDefaults().withName("First Clinic").build();
        allClinics.add(clinic1, "admin");
        Clinic clinic2 = ClinicBuilder.startRecording().withDefaults().withName("Second Clinic").build();
        allClinics.add(clinic2, "admin");

        Clinician clinician1 = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic1).build();
        allClinicians.add(clinician1, "admin");
        Clinician clinician2 = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic2).build();
        allClinicians.add(clinician2, "admin");

        List<Clinician> clinicians = allClinicians.getAll();

        assertNotNull(clinicians);
        assertEquals(2, clinicians.size());
        assertEquals(clinic1, clinicians.get(0).getClinic());
        assertEquals(clinic2, clinicians.get(1).getClinic());

        markForDeletion(asList(clinic1, clinic2));
        markForDeletion(asList(clinician1, clinician2));
    }
}
