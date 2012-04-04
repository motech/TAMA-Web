package org.motechproject.tama.refdata.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.refdata.builder.DrugBuilder;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationRefDataContext.xml", inheritLocations = false)
public class DrugsTest extends SpringIntegrationTest {

    @Autowired
    AllDrugs allDrugs;

    @Test
    public void shouldGetDrugs() {
        Drug drug1 = DrugBuilder.startRecording().withId("1").withName("Drug1").build();
        Drug drug2 = DrugBuilder.startRecording().withId("2").withName("Drug2").build();
        allDrugs.add(drug1);
        allDrugs.add(drug2);

        assertEquals(2, allDrugs.getAll().size());

        markForDeletion(drug1);
        markForDeletion(drug2);
    }
}
