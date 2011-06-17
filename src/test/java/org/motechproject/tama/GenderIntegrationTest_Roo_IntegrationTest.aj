// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.GenderDataOnDemand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect GenderIntegrationTest_Roo_IntegrationTest {
    
    declare @type: GenderIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: GenderIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: GenderIntegrationTest: @Transactional;
    
    @Autowired
    private GenderDataOnDemand GenderIntegrationTest.dod;
    
    @Test
    public void GenderIntegrationTest.testCountGenders() {
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", dod.getRandomGender());
        long count = org.motechproject.tama.Gender.countGenders();
        org.junit.Assert.assertTrue("Counter for 'Gender' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void GenderIntegrationTest.testFindGender() {
        org.motechproject.tama.Gender obj = dod.getRandomGender();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to provide an identifier", id);
        obj = org.motechproject.tama.Gender.findGender(id);
        org.junit.Assert.assertNotNull("Find method for 'Gender' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Gender' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void GenderIntegrationTest.testFindAllGenders() {
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", dod.getRandomGender());
        long count = org.motechproject.tama.Gender.countGenders();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Gender', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<org.motechproject.tama.Gender> result = org.motechproject.tama.Gender.findAllGenders();
        org.junit.Assert.assertNotNull("Find all method for 'Gender' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Gender' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void GenderIntegrationTest.testFindGenderEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", dod.getRandomGender());
        long count = org.motechproject.tama.Gender.countGenders();
        if (count > 20) count = 20;
        java.util.List<org.motechproject.tama.Gender> result = org.motechproject.tama.Gender.findGenderEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Gender' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Gender' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void GenderIntegrationTest.testFlush() {
        org.motechproject.tama.Gender obj = dod.getRandomGender();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to provide an identifier", id);
        obj = org.motechproject.tama.Gender.findGender(id);
        org.junit.Assert.assertNotNull("Find method for 'Gender' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyGender(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Gender' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void GenderIntegrationTest.testMerge() {
        org.motechproject.tama.Gender obj = dod.getRandomGender();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to provide an identifier", id);
        obj = org.motechproject.tama.Gender.findGender(id);
        boolean modified =  dod.modifyGender(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        org.motechproject.tama.Gender merged = (org.motechproject.tama.Gender) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Gender' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void GenderIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", dod.getRandomGender());
        org.motechproject.tama.Gender obj = dod.getNewTransientGender(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Gender' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Gender' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void GenderIntegrationTest.testRemove() {
        org.motechproject.tama.Gender obj = dod.getRandomGender();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Gender' failed to provide an identifier", id);
        obj = org.motechproject.tama.Gender.findGender(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Gender' with identifier '" + id + "'", org.motechproject.tama.Gender.findGender(id));
    }
    
}
