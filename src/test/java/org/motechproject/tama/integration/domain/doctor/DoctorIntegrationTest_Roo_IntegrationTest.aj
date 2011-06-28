// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.integration.domain.doctor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

privileged aspect DoctorIntegrationTest_Roo_IntegrationTest {
    
    declare @type: DoctorIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: DoctorIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    @Autowired
    private DoctorDataOnDemand DoctorIntegrationTest.dod;
    
    @Test
    public void DoctorIntegrationTest.testCountDoctors() {
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        org.junit.Assert.assertTrue("Counter for 'Doctor' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void DoctorIntegrationTest.testFindDoctor() {
        Doctor obj = dod.getRandomDoctor();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        org.junit.Assert.assertNotNull("Find method for 'Doctor' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Doctor' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void DoctorIntegrationTest.testFindAllDoctors() {
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Doctor', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<Doctor> result = Doctor.findAllDoctors();
        org.junit.Assert.assertNotNull("Find all method for 'Doctor' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Doctor' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void DoctorIntegrationTest.testFindDoctorEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        if (count > 20) count = 20;
        java.util.List<Doctor> result = Doctor.findDoctorEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Doctor' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Doctor' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void DoctorIntegrationTest.testFlush() {
        Doctor obj = dod.getRandomDoctor();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        org.junit.Assert.assertNotNull("Find method for 'Doctor' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyDoctor(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Doctor' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void DoctorIntegrationTest.testMerge() {
        Doctor obj = dod.getRandomDoctor();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        boolean modified =  dod.modifyDoctor(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        Doctor merged = (Doctor) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Doctor' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void DoctorIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        Doctor obj = dod.getNewTransientDoctor(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Doctor' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Doctor' identifier to no longer be null", obj.getId());
    }
    
    @Test(expected = org.ektorp.DocumentNotFoundException.class)
    public void DoctorIntegrationTest.testRemove() {
        Doctor obj = dod.getRandomDoctor();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        String id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Doctor' with identifier '" + id + "'", Doctor.findDoctor(id));
    }
    
}
