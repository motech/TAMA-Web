package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class DosageTypesViewTest {

    private DosageTypesView dosageTypesView;

    @Mock
    private AllDosageTypes allDosageTypes;

    @Before
    public void setUp() {
        initMocks(this);
        dosageTypesView = new DosageTypesView(allDosageTypes);
    }

    @Test
    public void shouldSortByTypeAscending() {

        when(allDosageTypes.getAll()).thenReturn(new ArrayList<DosageType>() {
            {
                add(new DosageType("type2"));
                add(new DosageType("type1"));
            }
        });
        assertEquals("type1", dosageTypesView.getAll().get(0).getType());
        assertEquals("type2", dosageTypesView.getAll().get(1).getType());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(allDosageTypes.getAll()).thenReturn(new ArrayList<DosageType>() {
            {
                add(new DosageType("Type2"));
                add(new DosageType("type1"));
            }
        });
        assertEquals("type1", dosageTypesView.getAll().get(0).getType());
        assertEquals("Type2", dosageTypesView.getAll().get(1).getType());
    }

}
