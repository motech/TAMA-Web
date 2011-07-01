package org.motechproject.tama.integration.domain;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RegimenCompositionIntegrationTest extends SpringIntegrationTest {
    @Test
    public void shouldPersistRegimenComposition() {
        RegimenComposition regimenComposition = new RegimenComposition();
        regimenComposition.persist();

        RegimenComposition actualRegimenComposition = RegimenComposition.findRegimenComposition(regimenComposition.getId());
        Assert.assertNotNull(actualRegimenComposition);

        markForDeletion(actualRegimenComposition);
    }

    @Test
    public void mergeRegimenComposition() {
        RegimenComposition regimenComposition = new RegimenComposition();
        final Drug drugOne = new Drug();
        drugOne.setName("DrugOne");
        drugOne.persist();
        final Drug drugTwo = new Drug();
        drugTwo.setName("DrugTwo");
        drugTwo.persist();

        regimenComposition.setDrugs(new HashSet<Drug>() {{
            add(drugOne);
            add(drugTwo);
        }});
        regimenComposition.persist();

        Assert.assertEquals(2, regimenComposition.getDrugs().size());
        regimenComposition.setDrugs(new HashSet<Drug>() {{
            Drug drugTwo = new Drug();
            drugTwo.setName("DrugTwo");
            add(drugTwo);
        }});
        regimenComposition.merge();
       Assert.assertEquals(1, regimenComposition.getDrugs().size());

        markForDeletion(regimenComposition);
        markForDeletion(drugOne);
        markForDeletion(drugTwo);
    }

    @Test
    public void shouldReturnRegimenCount() {
        long numberOfRegimens = RegimenComposition.countRegimenCompositions();
        Assert.assertEquals(0, numberOfRegimens);

        RegimenComposition regimenComposition = new RegimenComposition();
        regimenComposition.persist();
        numberOfRegimens = RegimenComposition.countRegimenCompositions();
        Assert.assertEquals(1, numberOfRegimens);

        markForDeletion(regimenComposition);
    }
}
