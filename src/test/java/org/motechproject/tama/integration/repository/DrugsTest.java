package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.repository.Drugs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

public class DrugsTest extends SpringIntegrationTest {

    @Autowired
    Drugs drugs;

    @Test
    public void testShouldGetDrugs() {
        Drug drug1 = DrugBuilder.startRecording().withId("1").withName("Drug1").build();
        Drug drug2 = DrugBuilder.startRecording().withId("2").withName("Drug2").build();
        drugs.add(drug1);
        drugs.add(drug2);

        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("1");
        drugIds.add("2");

        Assert.assertEquals(2, drugs.getDrugs(drugIds).size());

        markForDeletion(drug1);
        markForDeletion(drug2);
    }
}
