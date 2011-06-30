package org.motechproject.tama.domain;

import junit.framework.Assert;
import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.repository.Doctors;
import org.motechproject.tama.repository.Drugs;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Drug.class)
public class DrugTest {

	@Test
	public void findByIdShouldReturnNullIfNotFound() {

        Drugs drugs = mock(Drugs.class);
        final String id = "id";
        when(drugs.get(id)).thenThrow(new DocumentNotFoundException("NotFoundPath")) ;

        PowerMockito.spy(Drug.class);
        when(Drug.drugs()).thenReturn(drugs);

        Drug drug = Drug.findDrug(id);
        Assert.assertNull(drug);
    }
	
}
