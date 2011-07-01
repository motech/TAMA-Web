package org.motechproject.tama.integration.domain;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegimenIntegrationTest extends SpringIntegrationTest{

    @Test
    public void shouldPersistRegimen() {
        Regimen regimen = new Regimen();
        regimen.setName("newRegimen");
        regimen.persist();

        Regimen actualRegimen = Regimen.findRegimen(regimen.getId());
        Assert.assertNotNull(actualRegimen);
        Assert.assertEquals(actualRegimen.getName(), actualRegimen.getName());

        markForDeletion(actualRegimen);
    }

    @Test
    public void mergeRegimen() {
        Regimen regimen = new Regimen();
        regimen.setName("regimenFirst");
        regimen.persist();

        Assert.assertEquals("regimenFirst", Regimen.findRegimen(regimen.getId()).getName());
        regimen.setName("regimenNext");

        regimen.merge();
        Assert.assertEquals("regimenNext", Regimen.findRegimen(regimen.getId()).getName());

        markForDeletion(regimen);
    }
//
    @Test
    public void shouldReturnRegimenCount() {
        long numberOfRegimens = Regimen.countRegimens();
        Assert.assertEquals(0, numberOfRegimens);

        Regimen regimen = new Regimen();
        regimen.persist();
        numberOfRegimens = Regimen.countRegimens();
        Assert.assertEquals(1, numberOfRegimens);

        markForDeletion(regimen);
    }

    @Test
    public void shouldFindAllRegimens() {

        Regimen regimenOne = new Regimen();
        Regimen regimenTwo = new Regimen();

        List<Regimen> regimenList = Regimen.findAllRegimens();
        Assert.assertTrue(regimenList.isEmpty());

        regimenOne.persist();
        regimenTwo.persist();
        regimenList = Regimen.findAllRegimens();

        Assert.assertEquals(2, regimenList.size());
        Assert.assertTrue(regimenList.containsAll(Arrays.asList(regimenOne, regimenTwo)));

        markForDeletion(regimenOne);
        markForDeletion(regimenTwo);
    }

    @Test
    public void shouldRemoveRegimens() {

        Regimen regimen = new Regimen();
        regimen.persist();

        String id = regimen.getId();
        regimen.remove();
        Assert.assertNull(Regimen.findRegimen(id));
    }

    @Test
    public void shouldAddRegimenAndPersist() {
        Regimen regimen = new Regimen();
        final RegimenComposition composition = new RegimenComposition();
        regimen.setCompositions(new HashSet<RegimenComposition>(){{
            add(composition);
        }});
        regimen.persist();
        Regimen persistedRegimen = Regimen.findRegimen(regimen.getId());

        Set<RegimenComposition> persistedRegimenCompositions = persistedRegimen.getCompositions();
        Assert.assertEquals(1, persistedRegimenCompositions.size());

        markForDeletion(regimen);
    }


}
