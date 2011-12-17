package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class ModesOfTransmissionViewTest {

    private ModesOfTransmissionView modesOfTransmissionView;

    @Mock
    private AllModesOfTransmission modesOfTransmission;

    @Before
    public void setUp() {
        initMocks(this);
        modesOfTransmissionView = new ModesOfTransmissionView(modesOfTransmission);
    }

    @Test
    public void shouldSortByTypeAscending() {

        when(modesOfTransmission.getAll()).thenReturn(new ArrayList<ModeOfTransmission>() {
            {
                add(new ModeOfTransmission() {{
                    setType("type2");
                }});
                add(new ModeOfTransmission() {{
                    setType("type1");
                }});
            }
        });
        assertEquals("type1", modesOfTransmissionView.getAll().get(0).getType());
        assertEquals("type2", modesOfTransmissionView.getAll().get(1).getType());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(modesOfTransmission.getAll()).thenReturn(new ArrayList<ModeOfTransmission>() {
            {
                add(new ModeOfTransmission() {{
                    setType("Type2");
                }});
                add(new ModeOfTransmission() {{
                    setType("type1");
                }});
            }
        });
        assertEquals("type1", modesOfTransmissionView.getAll().get(0).getType());
        assertEquals("Type2", modesOfTransmissionView.getAll().get(1).getType());
    }
}
