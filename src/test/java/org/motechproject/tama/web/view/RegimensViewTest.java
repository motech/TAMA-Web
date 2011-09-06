package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.repository.AllRegimens;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class RegimensViewTest {


    private RegimensView regimensView;

    @Mock
    private AllRegimens regimens;

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
        assertEquals("regimen1", regimensView.getAll().get(0).getDisplayName());
        assertEquals("regimen2", regimensView.getAll().get(1).getDisplayName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(regimens.getAll()).thenReturn(new ArrayList<Regimen>() {
            {
                add(new RegimenBuilder().withDefaults().withName("Regimen2").build());
                add(new RegimenBuilder().withDefaults().withName("regimen1").build());
            }
        });
        assertEquals("regimen1", regimensView.getAll().get(0).getDisplayName());
        assertEquals("Regimen2", regimensView.getAll().get(1).getDisplayName());
    }

}
