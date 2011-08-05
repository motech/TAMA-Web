package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class CliniciansViewTest {


    private CliniciansView cliniciansDropDownList;

    @Mock
    private Clinicians clinicians;

    @Before
    public void setUp() {
        initMocks(this);
        cliniciansDropDownList = new CliniciansView(clinicians);


    }

    @Test
    public void shouldSortByNameAscending() {
        when(clinicians.getAll()).thenReturn(new ArrayList<Clinician>() {
            {
                add(new ClinicianBuilder().withDefaults().withName("clinician2").build());
                add(new ClinicianBuilder().withDefaults().withName("clinician1").build());
            }
        });
        assertEquals("clinician1", cliniciansDropDownList.getAll().get(0).getName());
        assertEquals("clinician2", cliniciansDropDownList.getAll().get(1).getName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {
        when(clinicians.getAll()).thenReturn(new ArrayList<Clinician>() {
            {
                add(new ClinicianBuilder().withDefaults().withName("Clinician2").build());
                add(new ClinicianBuilder().withDefaults().withName("clinician1").build());
            }
        });
        assertEquals("clinician1", cliniciansDropDownList.getAll().get(0).getName());
        assertEquals("Clinician2", cliniciansDropDownList.getAll().get(1).getName());
    }


}
