package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.repository.Regimens;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class RegimensViewTest {


    private RegimensView regimensView;

    @Mock
    private Regimens regimens;

    @Before
    public void setUp() {
        initMocks(this);
        regimensView = new RegimensView(regimens);
    }

    @Test
    public void shouldSortByDisplayNameAscending() {

        when(regimens.getAll()).thenReturn(new ArrayList<Regimen>() {
            {
                add(new RegimenBuilder().withDefaults().withName("regimen2").build());
                add(new RegimenBuilder().withDefaults().withName("regimen1").build());
            }
        });
        assertEquals("regimen1", regimensView.getAll().get(0).getRegimenDisplayName());
        assertEquals("regimen2", regimensView.getAll().get(1).getRegimenDisplayName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(regimens.getAll()).thenReturn(new ArrayList<Regimen>() {
            {
                add(new RegimenBuilder().withDefaults().withName("Regimen2").build());
                add(new RegimenBuilder().withDefaults().withName("regimen1").build());
            }
        });
        assertEquals("regimen1", regimensView.getAll().get(0).getRegimenDisplayName());
        assertEquals("Regimen2", regimensView.getAll().get(1).getRegimenDisplayName());
    }

}
