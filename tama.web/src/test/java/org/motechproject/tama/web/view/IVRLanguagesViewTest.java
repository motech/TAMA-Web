package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class IVRLanguagesViewTest {

    private IVRLanguagesView IVRLanguagesView;

    @Mock
    private AllIVRLanguages allIVRLanguages;

    @Before
    public void setUp() {
        initMocks(this);
        IVRLanguagesView = new IVRLanguagesView(allIVRLanguages);
    }

    @Test
    public void shouldSortByNameAscending() {

        when(allIVRLanguages.getAll()).thenReturn(new ArrayList<IVRLanguage>() {
            {
                add(new IVRLanguage() {{
                    setName("language2");
                }});
                add(new IVRLanguage() {{
                    setName("language1");
                }});
            }
        });
        assertEquals("language1", IVRLanguagesView.getAll().get(0).getName());
        assertEquals("language2", IVRLanguagesView.getAll().get(1).getName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(allIVRLanguages.getAll()).thenReturn(new ArrayList<IVRLanguage>() {
            {
                add(new IVRLanguage() {{
                    setName("Language2");
                }});
                add(new IVRLanguage() {{
                    setName("language1");
                }});
            }
        });
        assertEquals("language1", IVRLanguagesView.getAll().get(0).getName());
        assertEquals("Language2", IVRLanguagesView.getAll().get(1).getName());
    }
}
