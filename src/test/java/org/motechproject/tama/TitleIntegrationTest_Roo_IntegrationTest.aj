// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.TitleDataOnDemand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect TitleIntegrationTest_Roo_IntegrationTest {
    
    declare @type: TitleIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: TitleIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: TitleIntegrationTest: @Transactional;
    
    @Autowired
    private TitleDataOnDemand TitleIntegrationTest.dod;
    
    @Test
    public void TitleIntegrationTest.testCountTitles() {
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", dod.getRandomTitle());
        long count = org.motechproject.tama.Title.countTitles();
        org.junit.Assert.assertTrue("Counter for 'Title' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void TitleIntegrationTest.testFindTitle() {
        org.motechproject.tama.Title obj = dod.getRandomTitle();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to provide an identifier", id);
        obj = org.motechproject.tama.Title.findTitle(id);
        org.junit.Assert.assertNotNull("Find method for 'Title' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Title' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void TitleIntegrationTest.testFindAllTitles() {
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", dod.getRandomTitle());
        long count = org.motechproject.tama.Title.countTitles();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Title', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<org.motechproject.tama.Title> result = org.motechproject.tama.Title.findAllTitles();
        org.junit.Assert.assertNotNull("Find all method for 'Title' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Title' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void TitleIntegrationTest.testFindTitleEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", dod.getRandomTitle());
        long count = org.motechproject.tama.Title.countTitles();
        if (count > 20) count = 20;
        java.util.List<org.motechproject.tama.Title> result = org.motechproject.tama.Title.findTitleEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Title' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Title' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void TitleIntegrationTest.testFlush() {
        org.motechproject.tama.Title obj = dod.getRandomTitle();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to provide an identifier", id);
        obj = org.motechproject.tama.Title.findTitle(id);
        org.junit.Assert.assertNotNull("Find method for 'Title' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyTitle(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Title' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void TitleIntegrationTest.testMerge() {
        org.motechproject.tama.Title obj = dod.getRandomTitle();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to provide an identifier", id);
        obj = org.motechproject.tama.Title.findTitle(id);
        boolean modified =  dod.modifyTitle(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        org.motechproject.tama.Title merged = (org.motechproject.tama.Title) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Title' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void TitleIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", dod.getRandomTitle());
        org.motechproject.tama.Title obj = dod.getNewTransientTitle(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Title' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Title' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void TitleIntegrationTest.testRemove() {
        org.motechproject.tama.Title obj = dod.getRandomTitle();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Title' failed to provide an identifier", id);
        obj = org.motechproject.tama.Title.findTitle(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Title' with identifier '" + id + "'", org.motechproject.tama.Title.findTitle(id));
    }
    
}
