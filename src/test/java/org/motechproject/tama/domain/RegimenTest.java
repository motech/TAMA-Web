package org.motechproject.tama.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.repository.Regimens;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Regimen.class)
public class RegimenTest {

    @Test
    public void findByIdShouldReturnNullIfNotFound() {

        Regimens regimens = mock(Regimens.class);
        final String id = "id";
        when(regimens.get(id)).thenThrow(new DocumentNotFoundException("NotFoundPath")) ;

        PowerMockito.spy(Regimen.class);
        when(Regimen.regimens()).thenReturn(regimens);

        Regimen regimen = Regimen.findRegimen(id);
        Assert.assertNull(regimen);
    }

}
