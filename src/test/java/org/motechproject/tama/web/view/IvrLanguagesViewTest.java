package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.IVRLanguages;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class IvrLanguagesViewTest {


    private IvrLanguagesView ivrLanguagesView;

    @Mock
    private IVRLanguages ivrLanguages;

    @Before
    public void setUp() {
        initMocks(this);
        ivrLanguagesView = new IvrLanguagesView(ivrLanguages);
    }

    @Test
    public void shouldSortByNameAscending() {

        when(ivrLanguages.getAll()).thenReturn(new ArrayList<IVRLanguage>() {
            {
                add(new IVRLanguage(){{ setName("language2");}});
                add(new IVRLanguage(){{ setName("language1");}});
            }
        });
        assertEquals("language1", ivrLanguagesView.getAll().get(0).getName());
        assertEquals("language2", ivrLanguagesView.getAll().get(1).getName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(ivrLanguages.getAll()).thenReturn(new ArrayList<IVRLanguage>() {
            {
                add(new IVRLanguage(){{ setName("Language2");}});
                add(new IVRLanguage(){{ setName("language1");}});
            }
        });
        assertEquals("language1", ivrLanguagesView.getAll().get(0).getName());
        assertEquals("Language2", ivrLanguagesView.getAll().get(1).getName());
    }


}
