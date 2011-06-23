// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.PatientDataOnDemand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PatientIntegrationTest_Roo_IntegrationTest {
    
    declare @type: PatientIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: PatientIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: PatientIntegrationTest: @Transactional;
    
    @Autowired
    private PatientDataOnDemand PatientIntegrationTest.dod;
    
    @Test
    public void PatientIntegrationTest.testCountPatients() {
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", dod.getRandomPatient());
        long count = org.motechproject.tama.Patient.countPatients();
        org.junit.Assert.assertTrue("Counter for 'Patient' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void PatientIntegrationTest.testFindPatient() {
        org.motechproject.tama.Patient obj = dod.getRandomPatient();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to provide an identifier", id);
        obj = org.motechproject.tama.Patient.findPatient(id);
        org.junit.Assert.assertNotNull("Find method for 'Patient' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Patient' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void PatientIntegrationTest.testFindAllPatients() {
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", dod.getRandomPatient());
        long count = org.motechproject.tama.Patient.countPatients();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Patient', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<org.motechproject.tama.Patient> result = org.motechproject.tama.Patient.findAllPatients();
        org.junit.Assert.assertNotNull("Find all method for 'Patient' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Patient' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void PatientIntegrationTest.testFindPatientEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", dod.getRandomPatient());
        long count = org.motechproject.tama.Patient.countPatients();
        if (count > 20) count = 20;
        java.util.List<org.motechproject.tama.Patient> result = org.motechproject.tama.Patient.findPatientEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Patient' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Patient' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void PatientIntegrationTest.testFlush() {
        org.motechproject.tama.Patient obj = dod.getRandomPatient();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to provide an identifier", id);
        obj = org.motechproject.tama.Patient.findPatient(id);
        org.junit.Assert.assertNotNull("Find method for 'Patient' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPatient(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Patient' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PatientIntegrationTest.testMerge() {
        org.motechproject.tama.Patient obj = dod.getRandomPatient();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to provide an identifier", id);
        obj = org.motechproject.tama.Patient.findPatient(id);
        boolean modified =  dod.modifyPatient(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        org.motechproject.tama.Patient merged = (org.motechproject.tama.Patient) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Patient' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PatientIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", dod.getRandomPatient());
        org.motechproject.tama.Patient obj = dod.getNewTransientPatient(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Patient' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Patient' identifier to no longer be null", obj.getId());
    }
    
    @Test(expected = org.ektorp.DocumentNotFoundException.class)
    public void PatientIntegrationTest.testRemove() {
        org.motechproject.tama.Patient obj = dod.getRandomPatient();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Patient' failed to provide an identifier", id);
        obj = org.motechproject.tama.Patient.findPatient(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Patient' with identifier '" + id + "'", org.motechproject.tama.Patient.findPatient(id));
    }
    
}
