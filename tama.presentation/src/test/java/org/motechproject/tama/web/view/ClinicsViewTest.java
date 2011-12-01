package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamadomain.builder.ClinicBuilder;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.repository.AllClinics;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class ClinicsViewTest {


    private ClinicsView clinicsView;

    @Mock
    private AllClinics allClinics;

    @Before
    public void setUp() {
        initMocks(this);
        clinicsView = new ClinicsView(allClinics);
    }

    @Test
    public void shouldSortByNameAscending() {
        when(allClinics.getAll()).thenReturn(new ArrayList<Clinic>() {
            {
                add(new ClinicBuilder().withDefaults().withName("clinic2").build());
                add(new ClinicBuilder().withDefaults().withName("clinic1").build());
            }
        });
        assertEquals("clinic1", clinicsView.getAll().get(0).getName());
        assertEquals("clinic2", clinicsView.getAll().get(1).getName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {
        when(allClinics.getAll()).thenReturn(new ArrayList<Clinic>() {
            {
                add(new ClinicBuilder().withDefaults().withName("Clinic2").build());
                add(new ClinicBuilder().withDefaults().withName("clinic1").build());
            }
        });
        assertEquals("clinic1", clinicsView.getAll().get(0).getName());
        assertEquals("Clinic2", clinicsView.getAll().get(1).getName());
    }


}
