package org.motechproject.tamadomain.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tamadomain.builder.DrugBuilder;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.Drug;
import org.motechproject.tamadomain.repository.AllDrugs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

public class DrugsTest extends SpringIntegrationTest {

    @Autowired
    AllDrugs allDrugs;

    @Test
    public void testShouldGetDrugs() {
        Drug drug1 = DrugBuilder.startRecording().withId("1").withName("Drug1").build();
        Drug drug2 = DrugBuilder.startRecording().withId("2").withName("Drug2").build();
        allDrugs.add(drug1);
        allDrugs.add(drug2);

        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("1");
        drugIds.add("2");

        Assert.assertEquals(2, allDrugs.getDrugs(drugIds).size());

        markForDeletion(drug1);
        markForDeletion(drug2);
    }
}
