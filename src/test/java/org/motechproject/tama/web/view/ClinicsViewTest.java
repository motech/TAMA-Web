package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.Clinics;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class ClinicsViewTest {


    private ClinicsView clinicsView;

    @Mock
    private Clinics clinics;

    @Before
    public void setUp() {
        initMocks(this);
        clinicsView = new ClinicsView(clinics);
    }

    @Test
    public void shouldSortByNameAscending() {
        when(clinics.getAll()).thenReturn(new ArrayList<Clinic>() {
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
        when(clinics.getAll()).thenReturn(new ArrayList<Clinic>() {
            {
                add(new ClinicBuilder().withDefaults().withName("Clinic2").build());
                add(new ClinicBuilder().withDefaults().withName("clinic1").build());
            }
        });
        assertEquals("clinic1", clinicsView.getAll().get(0).getName());
        assertEquals("Clinic2", clinicsView.getAll().get(1).getName());
    }


}
